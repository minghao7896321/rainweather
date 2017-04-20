package com.rainweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lenovo on 2017/4/5.
 */

public class Basic {

    @SerializedName("city") // 使用注解使JSON字段和Java字段建立映射关系，因为JSON的一些字段不利于阅读
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update { // 建立内部类以与JSON字段保持一致

        @SerializedName("loc")
        public String updateTime;

    }
}
