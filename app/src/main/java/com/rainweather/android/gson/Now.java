package com.rainweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lenovo on 2017/4/5.
 */

public class Now {

    @SerializedName("cond") // condition
    public Situation situation;

    public class Situation {

        @SerializedName("code")
        public String weathercode;

        @SerializedName("txt")
        public String weatherinfo;

    }

    @SerializedName("hum") // 湿度
    public String humidity;

    @SerializedName("pres") // 气压
    public String pressure;

    @SerializedName("tmp")
    public String temperature;

    public Wind wind;

    public class Wind {

        @SerializedName("dir") // 风向
        public String direction;

        @SerializedName("sc") // 风力
        public String windpower;

    }

}
