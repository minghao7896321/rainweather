package com.rainweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lenovo on 2017/4/5.
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast") // 使用集合类型对未来几天的天气进行声明，Forecast中只定义单日天气
    public List<Forecast> forecastList;

}
