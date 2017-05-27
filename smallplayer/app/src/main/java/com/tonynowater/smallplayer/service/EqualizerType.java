package com.tonynowater.smallplayer.service;

/**
 * Created by tonynowater on 2017/5/23.
 */
public enum EqualizerType {
    STANDARD("標準"),
    CLASSICAL("古典"),
    DANCE("舞曲"),
    POP("流行"),
    ROCK("搖滾"),
    OPERA("聲樂"),
    JAZZ("爵士");

    private final String value;

    EqualizerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
