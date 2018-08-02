package io.agora.murder.mystery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import io.agora.murder.point.User;
import io.agora.murder.utils.ConstantApp;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class CrimeActivity extends Activity implements View.OnClickListener, AGApplication.OnAgoraEngineInterface {

    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;

    private RecyclerView mRecycleView;
    /**
     * 观看模式和参加模式共用同一套界面，通过 bIsBroadCaster 变量进行区分是观看者还是玩家
     */
    private boolean bIsBroadCaster = false;
    private RtcEngine mRtcEngine;
    private List<User> mUserList = new ArrayList<>();
    private RecycleViewAdapter mAdapter;
    private CheckBox mCheckBoxAudio;
    private CheckBox mCheckBoxMicphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        /***
         * 动态申请权限
         */
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {

            initAgoraEngineAndJoinChannel();
        }
    }

    private void setupData() {
        Intent i = getIntent();
        int cRole = i.getIntExtra(ConstantApp.ACTION_KEY_CROLE, 0);

        bIsBroadCaster = (cRole == Constants.CLIENT_ROLE_BROADCASTER);

        if (mRtcEngine != null) {

            /** 观看者不能说话，mute 自己**/
            if (!bIsBroadCaster) {
                mRtcEngine.muteLocalAudioStream(true);
            }
        }
    }

    private void setupUI() {

        if (bIsBroadCaster) {
            // 玩家模式界面
            findViewById(R.id.main_broadcast).setVisibility(View.VISIBLE);
            findViewById(R.id.main_audience).setVisibility(View.GONE);
        } else {
            // 观看者模式界面 （micphone 按钮不能按， 房间和走廊场景无法进入）
            findViewById(R.id.main_broadcast).setVisibility(View.GONE);
            findViewById(R.id.main_audience).setVisibility(View.VISIBLE);
        }

        mRecycleView = (RecyclerView) findViewById(R.id.main_memberlist);
        findViewById(R.id.main_bedroom).setOnClickListener(this);
        findViewById(R.id.main_corridor).setOnClickListener(this);

        /** recycleview 设置，当前默认设置一行4列 **/
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycleView.setLayoutManager(layoutManager);
        mAdapter = new RecycleViewAdapter(this, mUserList);
        mRecycleView.setAdapter(mAdapter);

        // 根据是玩家还是观看者，查找不同面板的控件
        if (bIsBroadCaster) {
            mCheckBoxAudio = (CheckBox) findViewById(R.id.main_audio);
        } else {

            mCheckBoxAudio = (CheckBox) findViewById(R.id.main_audience_audio);
        }

        mCheckBoxMicphone = (CheckBox) findViewById(R.id.main_micphone);

        mCheckBoxAudio.setOnCheckedChangeListener(onCheckedChangeListener);
        mCheckBoxMicphone.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    /**
     * 本地听筒 mute 按钮和 microphone mute 按钮状态切换，三个 activity 里面设置相同
     **/
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.main_audio:
                case R.id.main_audience_audio:
                    ConstantApp.LOCAL_AUDIO_MUTE = isChecked;
                    mRtcEngine.muteAllRemoteAudioStreams(isChecked);
                    break;
                case R.id.main_micphone:
                    ConstantApp.LOCAL_MICPHONE_MUTE = isChecked;
                    mRtcEngine.muteLocalAudioStream(isChecked);
                    break;
            }
        }
    };

    public boolean checkSelfPermission(String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initAgoraEngineAndJoinChannel();
                } else {
                    showLongToast("No permission for " + Manifest.permission.RECORD_AUDIO);
                    finish();
                }
                break;
            }

        }
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initAgoraEngineAndJoinChannel() {

        mRtcEngine = AGApplication.the().getmRtcEngine();

        setupData();
        setupUI();

        AGApplication.the().setOnAgoraEngineInterface(this);
        joinChannel(ConstantApp.CHANNEL_NAME_MAIN);

    }

    /***
     * 左上角返回按钮的点击事件
     * @param view
     */
    public void finishClick(View view) {

        leaveChannel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_bedroom: {
                if (bIsBroadCaster) {
                    Intent i = new Intent(CrimeActivity.this, SceneActivity.class);
                    i.putExtra(ConstantApp.ACTION_KEY_TYPE_MODE, ConstantApp.MODE_BEDROOM);

                    startActivityForResult(i, ConstantApp.REQUEST_CODE);
                }

                break;
            }
            case R.id.main_corridor: {

                if (bIsBroadCaster) {
                    Intent i = new Intent(CrimeActivity.this, SceneActivity.class);
                    i.putExtra(ConstantApp.ACTION_KEY_TYPE_MODE, ConstantApp.MODE_CORRIDER);

                    startActivityForResult(i, ConstantApp.REQUEST_CODE);
                }

                break;
            }

        }
    }


    @Override
    public void onUserJoined(final int uid, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 当有用户加入时，添加到用户列表
                // 当有用户加入时，添加到用户列表
                // 注意：由于demo缺少业务服务器，所以当观众加入的时候，观众也会被加入用户列表，并在界面的列表显示成静音状态。 正式实现的话，通过业务服务器可以判断是参与游戏的玩家还是围观观众
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
                // 收到某个uid mute 状态后刷新人员列表
                int index = getUserIndex(uid);
                mUserList.get(index).setAudioMute(muted);
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onJoinChannelSuccess(final String channel, int uid, int elapsed) {
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
                            if (audioVolumeInfo.uid != 0) {
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveChannel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** 不是从场景界面返回时，需要重新加入大厅的群聊频道，重新设置activity setOnAgoraEngineInterface 回调注册 **/
        if (requestCode == ConstantApp.REQUEST_CODE && resultCode == 0) {
            if (mRtcEngine != null) {
                AGApplication.the().setOnAgoraEngineInterface(this);
                joinChannel(ConstantApp.CHANNEL_NAME_MAIN);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 全局共用mute状态，在其他activity 返回时需要刷新当前mute 状态
        if (mCheckBoxAudio != null) {
            mCheckBoxAudio.setChecked(ConstantApp.LOCAL_AUDIO_MUTE);
            mCheckBoxMicphone.setChecked(ConstantApp.LOCAL_MICPHONE_MUTE);

        }
    }

    /**
     * 离开频道，重置听筒及麦克风按钮状态
     **/
    private void leaveChannel() {

        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        ConstantApp.LOCAL_AUDIO_MUTE = false;
        ConstantApp.LOCAL_MICPHONE_MUTE = false;
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
