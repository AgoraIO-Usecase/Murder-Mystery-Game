package io.agora.murder.mystery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import io.agora.murder.point.User;
import io.agora.murder.utils.ConstantApp;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class PrivateChatActivity extends Activity implements AGApplication.OnAgoraEngineInterface{

    private RecyclerView mRecycleView;
    private RtcEngine mRtcEngine;
    private List<User> mUserList = new ArrayList<>();
    private RecycleViewAdapter mAdapter;
    private CheckBox mCheckBoxAudio;
    private CheckBox mCheckBoxMicphone;
    private String mChannelName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);

        setupData();
        initAgoraEngineAndJoinChannel();
        setupUI();

    }

    private void setupData() {
        Intent i = getIntent();
        if (i != null) {
            mChannelName = i.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
        }

    }

    private void setupUI() {

        mRecycleView = (RecyclerView) findViewById(R.id.private_chat_memberlist);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycleView.setLayoutManager(layoutManager);
        mAdapter = new RecycleViewAdapter(this, mUserList);
        mRecycleView.setAdapter(mAdapter);

        mCheckBoxAudio = (CheckBox) findViewById(R.id.private_chat_audio);
        mCheckBoxMicphone = (CheckBox) findViewById(R.id.private_chat_micphone);

        mCheckBoxAudio.setOnCheckedChangeListener(onCheckedChangeListener);
        mCheckBoxMicphone.setOnCheckedChangeListener(onCheckedChangeListener);

        /** 根据当前的 mute 状态，设置对应的按钮 UI 状态**/
        mCheckBoxAudio.setChecked(ConstantApp.LOCAL_AUDIO_MUTE);
        mCheckBoxMicphone.setChecked(ConstantApp.LOCAL_MICPHONE_MUTE);

    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.private_chat_audio:
                    ConstantApp.LOCAL_AUDIO_MUTE = isChecked;
                    AGApplication.the().getmRtcEngine().muteAllRemoteAudioStreams(isChecked);
                    break;
                case R.id.private_chat_micphone:
                    ConstantApp.LOCAL_MICPHONE_MUTE = isChecked;
                    AGApplication.the().getmRtcEngine().muteLocalAudioStream(isChecked);
                    break;
            }
        }
    };


    private void initAgoraEngineAndJoinChannel() {
        AGApplication.the().setOnAgoraEngineInterface(this);

        mRtcEngine = AGApplication.the().getmRtcEngine();
        setupUI();
        /** 私聊和大厅是加入不同的频道 **/

        joinChannel(mChannelName);

    }

    @Override
    public void onUserJoined(final int uid, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 当有用户加入时，添加到用户列表
                mUserList.add(new User(uid, ConstantApp.ARR_NAMES[new Random().nextInt(ConstantApp.ARR_NAMES.length)],
                        ConstantApp.ARR_IMAGES[new Random().nextInt(ConstantApp.ARR_IMAGES.length)], false));
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 当用户离开时，从用户列表中清除
                mUserList.remove(getUserIndex(uid));
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onUserMuteAudio(final int uid, final boolean muted) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = getUserIndex(uid);
                mUserList.get(index).setAudioMute(muted);
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 注意：
                 * 1. 由于demo欠缺业务服务器，所以用户列表是根据 IRtcEngineEventHandler 的 onUserJoined、onUserOffline 回调来管理的
                 * 2. 每次加入频道成功后，清除列表，重新刷新用户数据
                 */

                mUserList.clear();
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAudioVolumeIndication(final IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (speakers != null) {
                    for (IRtcEngineEventHandler.AudioVolumeInfo audioVolumeInfo : speakers) {
                        if (audioVolumeInfo.volume > 0) {
                            if (audioVolumeInfo.uid != 0){
                                int index = getUserIndex(audioVolumeInfo.uid);
                                if (index >= 0) {
                                    mUserList.get(index).setSpeaking(true);
                                }
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    private int getUserIndex(int uid) {
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).getUid() == uid) {
                return i;
            }
        }
        return -1;
    }

    public void finishClick(View view) {
        leaveChannel();
    }

    @Override
    public void onBackPressed() {
        leaveChannel();
    }

    private void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }

        finish();
    }

    private void joinChannel(String channelName) {
        if (mRtcEngine != null) {
            /** 模式默认用通信模式 **/
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
            // 通信模式下默认为听筒，demo中将它切为外放
            mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
            mRtcEngine.enableAudioVolumeIndication(1000, 3);
            mRtcEngine.joinChannel(null, channelName, "", 0);
        }
    }

}
