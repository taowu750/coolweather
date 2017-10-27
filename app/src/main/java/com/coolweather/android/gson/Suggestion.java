package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 和风天气接口返回数据中的 suggestion json 对象，包含有 comf json 对象、cw json 对象和 sport json 对象，<br/>
 * 它们都只有一个字段 txt
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;


    public class Comfort {

        @SerializedName("txt")
        public String info;
    }

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }
}
