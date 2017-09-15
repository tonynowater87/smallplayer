package com.tonynowater.smallplayer.util;

/**
 * Created by tonyliao on 2017/4/30.
 */
public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();

    private TimeUtil () {}

    public static String formatSongDuration (int songDuration) {
        songDuration /= 1000;
        int hour = songDuration / 3600;
        int minute = (songDuration % 3600) / 60;
        int second = songDuration % 60;
        return (hour > 0 ? hour + ":" : "")
                + (minute == 0 ? "00" : (hour > 0 && minute < 10) ? "0" + minute : minute)
                + (second < 10 ? ":0" + second : ":" + second);
    }
}
