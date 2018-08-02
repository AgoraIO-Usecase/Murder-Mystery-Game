package io.agora.murder.utils;


import io.agora.murder.mystery.R;

public class ConstantApp {

    public static final String ACTION_KEY_CROLE = "C_Role";
    public static final String ACTION_KEY_TYPE_MODE = "typeMode";
    public static final String ACTION_KEY_CHANNEL_NAME = "channelName";

    public static final String CHANNEL_NAME_MAIN = "crime";
    public static final String CHANNEL_NAME_BEDROOM = "bedroom";
    public static final String CHANNEL_NAME_CORRIDER = "hall";

    public static final int MODE_BEDROOM = 0X01;
    public static final int MODE_CORRIDER = 0X02;

    public static final int REQUEST_CODE = 0x01;
    public static final int RESULT_CODE_BACK = 0x02;
    public static final int RESULT_CODE_PRIVATE_CHAT = 0x03;

    /** 全局共用音频接收以及micphone 状态**/
    public static boolean LOCAL_AUDIO_MUTE = false;
    public static boolean LOCAL_MICPHONE_MUTE = false;

    public static String[] ARR_NAMES = new String[]{
            "李小姐", "老司机", "张阿姨", "赵铁柱"
    };

    public static int[] ARR_IMAGES = new int[]{
            R.drawable.manager, R.drawable.driver, R.drawable.miss, R.drawable.nurse
    };



}
