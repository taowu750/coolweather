package com.coolweather.android.util;

import android.util.Log;

import com.coolweather.android.exception.NotCreatedObjectException;

/**
 * 日志工具帮助类，用于控制日志打印的行为。
 */

public class LogUtil {

    /**
     * 分别对应着不同的日志级别，可以赋给 level 变量来控制打印何种日志。
     */
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTTHING = 6;

    /**
     * 表示当前日志的打印级别，比 level 低的日志级别信息将不会被打印，最高为 NOTHING。
     */
    public static int level = VERBOSE;

    /**
     * 本项目中日志 tag 的头标记，用在日志过滤器中。
     */
    public static final String TAG_HEAD = "WuT.";


    private LogUtil() {
        throw new NotCreatedObjectException();
    }


    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Log.e(tag, msg);
        }
    }
}
