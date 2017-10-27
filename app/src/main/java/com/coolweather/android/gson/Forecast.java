package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 和风天气接口返回数据中的 daily_forecast json 对象，这个对象比较特殊，它包含的是一个 json 数组，<br/>
 * 数组中每一项都包含着未来一天的天气。所以定义一个 Forecast 类表示单日天气，然后在声明实体类引用的<br/>
 * 时候使用集合类型来进行声明。<br/>
 *
 * Forecast 中包含有一个 date 字段，cond json 对象（它包含有一个 txt_d 字段），以及一个 tmp json 对象,<br/>
 * 它包含有一个 max 字段和一个 min 字段。
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;


    public class Temperature {

        public String max;

        public String min;
    }

    public class More {

        @SerializedName("txt_d")
        public String info;
    }
}
