package com.coolweather.android.gson;

/**
 * 和风天气接口返回数据中的 aqi(空气质量指数) json 对象，包括一个 city json 对象，其中有 aqi 字段 和 pm25 字段。
 */

public class AQI {

    public AQICity city;


    public class AQICity {

        public String aqi;

        public String pm25;
    }
}
