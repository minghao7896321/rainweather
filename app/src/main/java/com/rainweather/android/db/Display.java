package com.rainweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2017/4/14.
 */

public class Display extends DataSupport{

    private int id;

    private String countyName;

    private String weatherId;

    private String distemperature;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getDistemperature() {
        return distemperature;
    }

    public void setDistemperature(String distemperature) {
        this.distemperature = distemperature;
    }

}
