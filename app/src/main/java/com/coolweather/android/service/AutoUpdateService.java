package com.coolweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.coolweather.android.app.BaseApplication;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.ui.WeatherActivity;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.WeatherDataParseUtil;
import com.coolweather.android.util.WeatherInfoCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    public static final String BING_PIC_ADDR = "http://guolin.tech/api/bing_pic";


    private static final String TAG = LogUtil.TAG_HEAD + "AutoUpdateService";


    private WeatherBinder binder;
    private WeatherActivity activity;


    public AutoUpdateService() {
        binder = new WeatherBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        if (activity != null) {
            replaceBackground(activity);
        }

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
        LogUtil.v(TAG, "updateWeather: 开始更新天气信息");

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

                        LogUtil.v(TAG, "updateWeather: 更新天气信息成功");
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        LogUtil.v(TAG, "updateBingPic: 开始更新必应每日一图信息");

        HttpUtil.sendRequest(BING_PIC_ADDR, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "updateBingPic: IOException-" + Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                WeatherInfoCache.putBingPic(bingPic);

                LogUtil.v(TAG, "updateBingPic: 更新成功");
            }
        });
    }

    private void replaceBackground(WeatherActivity activity) {
        LogUtil.v(TAG, "replaceBackground: 开始更新 WeatherActivity 背景图片");

        String picAddr = WeatherInfoCache.getBingPic();
        if (picAddr != null) {
            new Thread(() -> {
                try {
                    URL url = new URL(picAddr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream is = connection.getInputStream();

                    String name = picAddr.substring(picAddr.lastIndexOf("/") + 1);
                    Drawable drawable = Drawable.createFromStream(is, name);
                    activity.runOnUiThread(() -> activity.setBackground(drawable));

                    LogUtil.v(TAG, "replaceBackground: 更新 WeatherActivity 背景图片成功");
                } catch (IOException e) {
                    LogUtil.e(TAG, "replaceBackground: IOException-" + Log.getStackTraceString(e));
                }
            }).start();
        }
    }


    public class WeatherBinder extends Binder {

        public void setWeatherActivity(WeatherActivity activity) {
            AutoUpdateService.this.activity = activity;
        }
    }
}
