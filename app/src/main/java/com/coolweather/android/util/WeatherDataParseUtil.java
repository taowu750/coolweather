package com.coolweather.android.util;

import android.util.Log;

import com.coolweather.android.exception.NotCreatedObjectException;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用来解析从网络上获取的天气数据的工具类。
 */

public class WeatherDataParseUtil {

    public static final String WEATHER_ADDRESS = "http://guolin.tech/api/weather";
    public static final String WEATHER_STATUS_OK = "ok";


    private static final String TAG = LogUtil.TAG_HEAD + "WeatherDataParseUtil";


    private WeatherDataParseUtil() {
        throw new NotCreatedObjectException();
    }


    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();

            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            LogUtil.e(TAG, "handleWeatherResponse: JSONException-" + Log.getStackTraceString(e));
        }

        return null;
    }
}
