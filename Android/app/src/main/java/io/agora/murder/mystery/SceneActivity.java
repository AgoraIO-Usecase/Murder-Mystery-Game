package io.agora.murder.mystery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.agora.murder.utils.ConstantApp;

public class SceneActivity extends Activity implements View.OnClickListener {

    private TextView mTitleView;
    private RelativeLayout mParentView;
    private CheckBox mCheckBoxAudio;
    private CheckBox mCheckBoxMicphone;
    private int mCurrentMode = ConstantApp.MODE_BEDROOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        setupData();
        setupUI();
    }

    private void setupData() {
        Intent i = getIntent();
        if (i != null) {
            mCurrentMode = i.getIntExtra(ConstantApp.ACTION_KEY_TYPE_MODE, ConstantApp.MODE_BEDROOM);
        }
    }

    private void setupUI() {

        mParentView = (RelativeLayout) findViewById(R.id.scene_parent);
        mTitleView = (TextView) findViewById(R.id.scene_title_textview);

        /** 根据场景mode 绘制不同的场景 **/
        if (mCurrentMode == ConstantApp.MODE_BEDROOM) {
            mTitleView.setText(getString(R.string.app_str_bedroom));
            mParentView.setBackgroundResource(R.drawable.bg04);
        } else {
            mTitleView.setText(getString(R.string.app_str_corrider));
            mParentView.setBackgroundResource(R.drawable.bg02);
        }
        findViewById(R.id.scene_private_chat).setOnClickListener(this);

        mCheckBoxAudio = (CheckBox) findViewById(R.id.scene_audio);
        mCheckBoxMicphone = (CheckBox) findViewById(R.id.scene_micphone);

        mCheckBoxAudio.setOnCheckedChangeListener(onCheckedChangeListener);
        mCheckBoxMicphone.setOnCheckedChangeListener(onCheckedChangeListener);


    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.scene_audio:
                    ConstantApp.LOCAL_AUDIO_MUTE = isChecked;
                    AGApplication.the().getmRtcEngine().muteAllRemoteAudioStreams(isChecked);
                    break;
                case R.id.scene_micphone:
                    ConstantApp.LOCAL_MICPHONE_MUTE = isChecked;
                    AGApplication.the().getmRtcEngine().muteLocalAudioStream(isChecked);
                    break;
            }
        }
    };

    public void finishClick(View view) {

        setResult(ConstantApp.RESULT_CODE_BACK);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scene_private_chat:
                /**
                 * 离开大厅群聊频道，启动私聊activity
                 * **/
                AGApplication.the().getmRtcEngine().leaveChannel();
                Intent intent = new Intent(SceneActivity.this, PrivateChatActivity.class);

                if (mCurrentMode == ConstantApp.MODE_BEDROOM) {
                    intent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, ConstantApp.CHANNEL_NAME_BEDROOM);
                } else {
                    intent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, ConstantApp.CHANNEL_NAME_CORRIDER);
                }

                startActivity(intent);
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        setResult(ConstantApp.RESULT_CODE_BACK);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** 根据当前的 mute 状态，设置对应的按钮 UI 状态**/
        if (mCheckBoxAudio != null) {
            mCheckBoxAudio.setChecked(ConstantApp.LOCAL_AUDIO_MUTE);
            mCheckBoxMicphone.setChecked(ConstantApp.LOCAL_MICPHONE_MUTE);
        }
    }

}
