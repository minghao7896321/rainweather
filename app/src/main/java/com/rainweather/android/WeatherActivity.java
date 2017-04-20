package com.rainweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rainweather.android.gson.Forecast;
import com.rainweather.android.gson.Weather;
import com.rainweather.android.util.HttpUtil;
import com.rainweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public SwipeRefreshLayout swipeRefreshLayout;

    private String weatherId;

    private ScrollView weatherLayout;

    private Button manageButton;

    private TextView titleCity;

    private TextView degreeText;

    private TextView weatherInfoText;

    private TextView pm25Text;

    private TextView weather2InfoText;

    private TextView degree2Text;

    private TextView weather3InfoText;

    private TextView degree3Text;

    private LinearLayout forecastLayout;

    private TextView comfortSign;

    private TextView comfortText;

    private TextView carWashSign;

    private TextView carWashText;

    private TextView drsgSign;

    private TextView drsgText;

    private TextView fluSign;

    private TextView fluText;

    private TextView sportSign;

    private TextView sportText;

    private TextView travSign;

    private TextView travText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        manageButton = (Button) findViewById(R.id.manage_button);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        weather2InfoText = (TextView) findViewById(R.id.weather2_info_text);
        degree2Text = (TextView) findViewById(R.id.degree2_text);
        weather3InfoText = (TextView) findViewById(R.id.weather3_info_text);
        degree3Text = (TextView) findViewById(R.id.degree3_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        comfortSign = (TextView) findViewById(R.id.comfortindex);
        comfortText = (TextView) findViewById(R.id.comfortinfo);
        carWashSign = (TextView) findViewById(R.id.carWashindex);
        carWashText = (TextView) findViewById(R.id.carWashinfo);
        drsgSign = (TextView) findViewById(R.id.drsgindex);
        drsgText = (TextView) findViewById(R.id.drsginfo);
        fluSign = (TextView) findViewById(R.id.fluindex);
        fluText = (TextView) findViewById(R.id.fluinfo);
        sportSign = (TextView) findViewById(R.id.sportindex);
        sportText = (TextView) findViewById(R.id.sportinfo);
        travSign = (TextView) findViewById(R.id.travindex);
        travText = (TextView) findViewById(R.id.travinfo);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        weatherId = getIntent().getStringExtra("weather_id");
        SharedPreferences prefs = getSharedPreferences("data1", MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);
        if(weatherString != null){
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weatherId == weather.basic.weatherId || weatherId == null) { //此处应该优化，并仔细斟酌weatherId在哪些情况下为空
                showWeatherInfo(weather);
            }
            else {
                weatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(weatherId);//相对在Manage里选择城市较慢，weatherId做文件名的可行性？
            }
        } else {
            //无缓存时去服务器查询天气
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, ManageAreaActivity.class);
                intent.putExtra("weather_id", weatherId);
                startActivity(intent);
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId){
        //String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        //String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=c0dcdf9494e24fa39deeadbe424c8394";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = getSharedPreferences("data1", MODE_PRIVATE).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            //weatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";
        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weather.now.situation.weatherinfo);
        pm25Text.setText(weather.aqi.city.pm25);
        int num = 0;
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.weather_d);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.situation.weatherinfo_d);
            maxText.setText(forecast.temperature.maxtemperature + "℃");
            minText.setText(forecast.temperature.mintemperature + "℃");
            forecastLayout.addView(view);
            if (num == 0) {
                String degree2 = forecast.temperature.maxtemperature + "/" + forecast.temperature.mintemperature + "℃";
                degree2Text.setText(degree2);
            }
            num++;
            if (num == 1){
                String degree3 = forecast.temperature.maxtemperature + "/" + forecast.temperature.mintemperature + "℃";
                degree3Text.setText(degree3);
                weather3InfoText.setText(forecast.situation.weatherinfo_d);
            }
        }
        weather2InfoText.setText(weather.now.situation.weatherinfo);
        comfortSign.setText(weather.suggestion.comfort.comfortindex);
        comfortText.setText(weather.suggestion.comfort.comfortinfo);
        carWashSign.setText(weather.suggestion.carWash.carWashindex);
        carWashText.setText(weather.suggestion.carWash.carWashinfo);
        drsgSign.setText(weather.suggestion.dress.drsgindex);
        drsgText.setText(weather.suggestion.dress.drsginfo);
        fluSign.setText(weather.suggestion.flu.fluindex);
        fluText.setText(weather.suggestion.flu.fluinfo);
        sportSign.setText(weather.suggestion.sport.sportindex);
        sportText.setText(weather.suggestion.sport.sportinfo);
        travSign.setText(weather.suggestion.travel.travindex);
        travText.setText(weather.suggestion.travel.travinfo);
        weatherLayout.setVisibility(View.VISIBLE);
    }

}
