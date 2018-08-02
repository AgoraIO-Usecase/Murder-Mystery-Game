package io.agora.murder.point;

/**
 * Created by yt on 2018/7/27/027.
 */

public class User {
    private int uid;
    private String name = "";
    private int imageSource;
    private boolean audioMute;
    private boolean isSpeaking;

    public User(int uid, String name, int imageIndex, boolean audioMute) {
        this.uid = uid;
        this.name = name;
        this.imageSource = imageIndex;
        this.audioMute = audioMute;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isAudioMute() {
        return audioMute;
    }

    public void setAudioMute(boolean audioMute) {
        this.audioMute = audioMute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }

    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }
}
