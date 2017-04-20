package com.rainweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lenovo on 2017/4/5.
 */

public class Forecast {

    @SerializedName("cond") // condition
    public Situation situation;

    public class Situation {

        @SerializedName("txt_d")
        public String weatherinfo_d;

        @SerializedName("txt_n")
        public String weatherinfo_n;

    }

    public String date;

    @SerializedName("hum") // 湿度
    public String humidity;

    @SerializedName("pres") // 气压
    public String pressure;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature {

        @SerializedName("max")
        public String maxtemperature;

        @SerializedName("min")
        public String mintemperature;

    }

    public Wind wind;

    public class Wind {

        @SerializedName("dir") // 风向
        public String direction;

        @SerializedName("sc") // 风力
        public String windpower;

    }

}
