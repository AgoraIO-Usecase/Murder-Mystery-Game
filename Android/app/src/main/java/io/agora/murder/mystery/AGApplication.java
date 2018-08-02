package io.agora.murder.mystery;

import android.app.Application;
import android.util.Log;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;


public class AGApplication extends Application {
    private final String TAG = AGApplication.class.getSimpleName();


    private static AGApplication mInstance;
    private RtcEngine mRtcEngine;

    public static AGApplication the() {
        return mInstance;
    }

    public AGApplication() {
        mInstance = this;
    }

    private OnAgoraEngineInterface onAgoraEngineInterface;

    /**
     * 声网频道内业务回调
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onUserOffline(int uid, int reason) {

            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onUserOffline(uid, reason);
            }

        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);

            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onUserJoined(uid, elapsed);
            }

        }

        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onUserMuteAudio(uid, muted);
            }

        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onJoinChannelSuccess(channel, uid, elapsed);
            }

        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
        }

        @Override
        public void onAudioVolumeIndication(AudioVolumeInfo[] speakers, int totalVolume) {
            super.onAudioVolumeIndication(speakers, totalVolume);
            if (onAgoraEngineInterface != null) {
                onAgoraEngineInterface.onAudioVolumeIndication(speakers,totalVolume);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        setupAgoraEngine();

    }

    public RtcEngine getmRtcEngine() {
        return mRtcEngine;
    }


    /**
     * 初始化声网 RtcEngine 对象
     */
    private void setupAgoraEngine() {
        String appID = getString(R.string.private_app_id);

        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appID, mRtcEventHandler);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    public void setOnAgoraEngineInterface(OnAgoraEngineInterface onAgoraEngineInterface) {
        this.onAgoraEngineInterface = onAgoraEngineInterface;
    }

    /**
     * 回调接口，需要接收声网SDK回调的类实现接口即可
     */
    public interface OnAgoraEngineInterface {

        void onUserJoined(int uid, int elapsed);

        void onUserOffline(int uid, int reason);

        void onUserMuteAudio(final int uid, final boolean muted);

        void onJoinChannelSuccess(String channel, int uid, int elapsed);

        void onAudioVolumeIndication(IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume);

    }

}

