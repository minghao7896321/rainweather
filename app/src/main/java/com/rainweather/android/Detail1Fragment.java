package com.rainweather.android;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainweather.android.gson.Forecast;
import com.rainweather.android.gson.Weather;
import com.rainweather.android.util.Utility;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lenovo on 2017/4/26.
 */

public class Detail1Fragment extends Fragment {

    /*private int num;

    public static Detail1Fragment newInstance(int num) {
        Detail1Fragment newFragment = new Detail1Fragment();
        Bundle bundle = new Bundle();
        bundle.putInt("num", num);
        newFragment.setArguments(bundle);
        return newFragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*Bundle args = getArguments();
        if (args != null) {
            num = args.getInt("num");
        }*/
        View view = inflater.inflate(R.layout.detail_1, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("data1", MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);
        Weather weather = Utility.handleWeatherResponse(weatherString);
        ImageView detail_image = (ImageView) view.findViewById(R.id.detail1_weatherImage);
        Resources resources = getContext().getResources();
        TextView detail_degreeText = (TextView) view.findViewById(R.id.detail1_degree_text);
        TextView detail_weather_infoText = (TextView) view.findViewById(R.id.detail1_weather_info_text);
        TextView directionText = (TextView) view.findViewById(R.id.direction1_text);
        TextView windpowerText = (TextView) view.findViewById(R.id.windpower1_text);
        TextView pressureText = (TextView) view.findViewById(R.id.pressure1_text);
        TextView humidityText = (TextView) view.findViewById(R.id.humidity1_text);
        Forecast forecast = weather.forecastList.get(0);
        int iconday = resources.getIdentifier("ic_" + forecast.situation.iconday, "drawable", getContext().getPackageName());
        if (iconday == 0) {
            detail_image.setImageResource(R.drawable.ic_999);
        } else {
            detail_image.setImageResource(iconday);
        }
        String degree2 = forecast.temperature.maxtemperature + "°/" + forecast.temperature.mintemperature + "°";
        detail_degreeText.setText(degree2);
        detail_weather_infoText.setText(forecast.situation.weatherinfo_d);
        if (forecast.wind.direction.equals("无持续风向")) {
            directionText.setTextSize(17);
        }
        directionText.setText(forecast.wind.direction);
        windpowerText.setText(forecast.wind.windpower + "级");
        pressureText.setText(forecast.pressure + "hPa");
        humidityText.setText(forecast.humidity + "%");
        return view;
    }
}
