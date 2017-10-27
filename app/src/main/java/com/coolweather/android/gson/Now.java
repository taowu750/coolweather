package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 和风天气接口返回数据中的 now json 对象，包括一个 tmp 字段和一个 cond json 对象，其中含有 txt 字段。
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;


    public class More {

        @SerializedName("txt")
        public String info;
    }
}
