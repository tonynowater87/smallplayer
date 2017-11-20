package com.tonynowater.smallplayer.util;

import android.util.Log;

/**
 * Created by tonyliao on 2017/4/29.
 */
public class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    private enum LogLevel {
        Debug(0)
        , Info(1)
        , Warn(2)
        , Error(3)
        , Verbose(4);

        private final int level;

        LogLevel(int level) {
            this.level = level;
        }
    }

    private static Logger instance = new Logger();
    private LogLevel mLogLever = LogLevel.Verbose;
    private boolean mIsEnableLog = true;

    public static Logger getInstance() {
        return instance;
    }

    Logger() {}

    public void d(String tag, String msg) {
        if (mLogLever.compareTo(LogLevel.Debug) < 0) {
            return;
        }
        if (mIsEnableLog) {
            Log.d(TAG, msg);
        }
    }

    public void i(String tag, String msg) {
        if (mLogLever.compareTo(LogLevel.Info) < 0) {
            return;
        }
        if (mIsEnableLog) {
            Log.i(TAG, msg);
        }
    }

    public void w(String tag, String msg) {
        if (mLogLever.compareTo(LogLevel.Warn) < 0) {
            return;
        }
        if (mIsEnableLog) {
            Log.w(TAG, msg);
        }
    }

    public void e(String tag, String msg) {
        if (mLogLever.compareTo(LogLevel.Warn) < 0) {
            return;
        }
        if (mIsEnableLog) {
            Log.e(TAG, msg);
        }
    }

    public void v(String tag, String msg) {
        if (mLogLever.compareTo(LogLevel.Verbose) < 0) {
            return;
        }
        if (mIsEnableLog) {
            Log.v(TAG, msg);
        }
    }
}
