package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 和风天气接口返回数据中的 basic json 对象，包括有城市名 city 字段，城市对应的天气 id 字段，还有一个<br/>
 * update json 对象，update 中有一个 loc 字段表示天气的更新时间。
 */

public class Basic {

    // 使用 SerializedName 注解的方式来让 json 字段与 java 字段之间建立联系
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;


    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}
