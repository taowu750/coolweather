package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.coolweather.android.app.BaseApplication;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.WeatherDataParseUtil;
import com.coolweather.android.util.WeatherInfoCache;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public static final String BING_PIC_ADDR = "http://guolin.tech/api/bing_pic";


    private static final String TAG = LogUtil.TAG_HEAD + "AutoUpdateService";


    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        // 之所以有一个取消操作，是因为开启这个服务的活动可能会多次启动服务，所以需要将上一次的定时任务结束
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 更新天气信息
     */
    private void updateWeather() {
        String weatherInfo = WeatherInfoCache.getWeatherInfo();
        if (weatherInfo != null) {
            Weather weather = WeatherDataParseUtil.handleWeatherResponse(weatherInfo);
            String weatherId = weather.basic.weatherId;
            String addr = WeatherDataParseUtil.WEATHER_ADDRESS + "?cityid=" + weatherId + "&key=" +
                    BaseApplication.HE_WEATHER_KEY;
            HttpUtil.sendRequest(addr, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogUtil.e(TAG, "updateWeather: IOException-" + Log.getStackTraceString(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather w = WeatherDataParseUtil.handleWeatherResponse(responseText);
                    if (w != null && WeatherDataParseUtil.WEATHER_STATUS_OK.equals(w.status)) {
                        WeatherInfoCache.putWeatherInfo(responseText);
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        HttpUtil.sendRequest(BING_PIC_ADDR, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "updateBingPic: IOException-" + Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                WeatherInfoCache.putBingPic(bingPic);
            }
        });
    }
}
