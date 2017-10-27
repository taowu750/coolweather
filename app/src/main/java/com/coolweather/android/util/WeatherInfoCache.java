package com.coolweather.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.coolweather.android.app.BaseApplication;
import com.coolweather.android.exception.NotCreatedObjectException;

/**
 * 天气数据的缓存类，用于将当前用户看到的天气数据缓存起来。
 */

public class WeatherInfoCache {

    private static final String TAG = LogUtil.TAG_HEAD + "WeatherInfoCache";

    private static final String WEATHER_INFO_PATH = "weather";
    private static final String WEATHER_INFO_KEY = "weather";
    private static final String WEATHER_INFO_TIME = "time";
    private static final String BING_PIC_KEY = "bing_pic";

    // 天气数据缓存的默认时间，ms 为单位，1 小时
    private static final long DEFAULT_MAX_REFRESH_TIME_INTERVAL = 60 * 60 * 1000L;


    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private static long maxRefreshTimeInterval = DEFAULT_MAX_REFRESH_TIME_INTERVAL;


    private WeatherInfoCache() {
        throw new NotCreatedObjectException();
    }


    /**
     * 如果存在缓存数据且没有过时，返回true，否则返回 false
     *
     * @return 缓存数据且没有过时，返回true，否则返回 false
     */
    public static boolean hasWeatherInfoCache() {
        if (prefs == null) {
            prefs = BaseApplication.getContext().getSharedPreferences(WEATHER_INFO_PATH, Context.MODE_PRIVATE);
        }
        long oldTime = prefs.getLong(WEATHER_INFO_TIME, -1L);
        if (oldTime != -1L) {
            long currentTime = System.currentTimeMillis();
            long interval = currentTime - oldTime;
            if (interval <= maxRefreshTimeInterval && interval >= 0) {
                LogUtil.d(TAG, "缓存数据没有过时，间隔为 " + (currentTime - oldTime) + ", 最大允许间隔为 " + maxRefreshTimeInterval);

                return true;
            }
        }

        return false;
    }

    /**
     * 获取上一次查看的天气数据，如果没有查看过天气，或者两次查看的时间间隔超过了<code>getMaxRefreshTimeInterval()<code/>
     * 方法返回的时间间隔（以毫秒为单位），则返回 null，否则返回相应的 json 数据字符串。
     *
     * @return 缓存的天气数据
     */
    public static String getWeatherInfo() {
        if (prefs == null) {
            prefs = BaseApplication.getContext().getSharedPreferences(WEATHER_INFO_PATH, Context.MODE_PRIVATE);
        }
        if (hasWeatherInfoCache()) {
            String weatherInfo = prefs.getString(WEATHER_INFO_KEY, null);

            LogUtil.d(TAG, "getWeatherInfo: 获取到了缓存的天气数据，时间是 " + prefs.getLong(WEATHER_INFO_TIME, -1L));

            return weatherInfo;
        }

        return null;
    }

    /**
     * 将天气数据缓存起来。
     *
     * @param weatherInfo 天气数据
     */
    public static void putWeatherInfo(String weatherInfo) {
        if (prefs == null) {
            prefs = BaseApplication.getContext().getSharedPreferences(WEATHER_INFO_PATH, Context.MODE_PRIVATE);
        }

        editor = prefs.edit();
        editor.putString(WEATHER_INFO_KEY, weatherInfo);
        editor.putLong(WEATHER_INFO_TIME, System.currentTimeMillis());
        editor.apply();

        LogUtil.d(TAG, "putWeatherInfo: 更新缓存的天气数据，时间是 " + prefs.getLong(WEATHER_INFO_TIME, -1L));
    }

    /**
     * 将必应每日一图信息保存到缓存中。
     *
     * @param bingPic 必应每日一图
     */
    public static void putBingPic(String bingPic) {
        if (prefs == null) {
            prefs = BaseApplication.getContext().getSharedPreferences(WEATHER_INFO_PATH, Context.MODE_PRIVATE);
        }

        editor = prefs.edit();
        editor.putString(BING_PIC_KEY, bingPic);
        editor.apply();

        LogUtil.d(TAG, "putBingPic: 更新缓存的必应每日一图-" + prefs.getString(BING_PIC_KEY, null));
    }

    /**
     * 获取刷新数据的最大时间间隔（默认为 10 分钟），以毫秒为单位。
     *
     * @return 刷新数据的最大时间间隔
     */
    public static long getMaxRefreshTimeInterval() {
        return maxRefreshTimeInterval;
    }

    /**
     * 设置刷新数据的最大时间间隔，以毫秒为单位。
     *
     * @param maxRefreshTimeInterval 刷新数据的最大时间间隔
     */
    public static void setMaxRefreshTimeInterval(long maxRefreshTimeInterval) {
        WeatherInfoCache.maxRefreshTimeInterval = maxRefreshTimeInterval;
    }
}
