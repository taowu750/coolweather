package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 和风天气接口返回数据中的总的数据对象，包含其他 json 对象。此外，返回的天气数据中还会包含一项 status 数据，<br/>
 * 成功返回 ok，失败返回具体的原因。
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
