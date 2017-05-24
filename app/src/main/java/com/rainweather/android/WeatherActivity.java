package com.rainweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rainweather.android.db.Display;
import com.rainweather.android.draw.DetailItemDecoration;
import com.rainweather.android.gson.Forecast;
import com.rainweather.android.gson.Weather;
import com.rainweather.android.service.AutoUpdateService;
import com.rainweather.android.util.HttpUtil;
import com.rainweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public static final int NUM = 7;

    private ImageView PicImg;

    private int code;

    public SwipeRefreshLayout swipeRefreshLayout;

    private String weatherId;

    //private ScrollView weatherLayout;

    private Button manageButton;

    private TextView titleCity;

    private TextView degreeText;

    private TextView weatherInfoText;

    private TextView levelText;

    private TextView weather2InfoText;

    private TextView degree2Text;

    private ImageView weather2Image;

    private TextView weather3InfoText;

    private TextView degree3Text;

    private ImageView weather3Image;

    private List<Forecast> mforecastList = new ArrayList<>();

    private RecyclerView mRecyclerView;

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
        //Glide.get(this).clearMemory();
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //初始化各控件
        PicImg = (ImageView) findViewById(R.id.pic_img);
        //weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        manageButton = (Button) findViewById(R.id.manage_button);
        titleCity = (TextView) findViewById(R.id.title_city);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        levelText = (TextView) findViewById(R.id.level_text);
        weather2InfoText = (TextView) findViewById(R.id.weather2_info_text);
        degree2Text = (TextView) findViewById(R.id.degree2_text);
        weather2Image = (ImageView) findViewById(R.id.weather2_image);
        weather3InfoText = (TextView) findViewById(R.id.weather3_info_text);
        degree3Text = (TextView) findViewById(R.id.degree3_text);
        weather3Image = (ImageView) findViewById(R.id.weather3_image);
        //近期预报控件
        mRecyclerView = (RecyclerView) findViewById(R.id.forecast_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DetailItemDecoration());//添加装饰类
        //生活指数控件
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

        weatherId = getIntent().getStringExtra("weather_id"); // 这个来源可以多加考虑

        /*if (weatherId != null) {
            //删除当前主页面的城市时如何发送上个城市的weatherId，肯定要在showWeatherInfo之前，怎么在从城市管理返回时加限定条件，比如广播？
            List<Display> displays = DataSupport.where("weatherId = ?", weatherId).find(Display.class);
            String text = "";
            for (Display olddisplay : displays) {
                text = olddisplay.getWeatherId();
            }
            if (text.equals("")) {
                displays = DataSupport.findAll(Display.class);
                for (Display olddisplay : displays) {
                    weatherId = olddisplay.getWeatherId();
                }
            }
        }*/

        SharedPreferences prefs = getSharedPreferences("data1", MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weatherId == weather.basic.weatherId || weatherId == null) { //此处应该优化，并仔细斟酌weatherId在哪些情况下为空：重新启动和从城市管理返回时
                loadPic(weather.now.situation.weathercode);
                showWeatherInfo(weather); // 这个函数里应不应该给weatherId赋值
            } else {
                //weatherLayout.setVisibility(View.INVISIBLE); // 暂时隐藏
                requestWeather(weatherId);//相对在Manage里选择城市较慢，weatherId做文件名的可行性？
            }
        } else {
            //无缓存时去服务器查询天气
            //weatherLayout.setVisibility(View.INVISIBLE); // 暂时隐藏
            requestWeather(weatherId);
        }
        mRecyclerView.setAdapter(fillDataToRecyclerView(mforecastList)); // 必须在获得数据后才能设置适配器
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
                intent.putExtra("now_weather_id", weatherId);
                startActivity(intent);
                finish(); // 必须finish，否则直接返回上次选择的城市，但是要改ManageAreaActivity的返回键
            }
        });
    }


    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String mweatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + mweatherId + "&key=af71e684a8f540feaaad7c92566324c1";
        //String weatherUrl = "http://guolin.tech/api/weather?cityid=" + mweatherId + "&key=c0dcdf9494e24fa39deeadbe424c8394";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            // 更新缓存文件
                            SharedPreferences.Editor editor = getSharedPreferences("data1", MODE_PRIVATE).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            //将当前城市温度写入Display表
                            weatherId = weather.basic.weatherId;
                            final String disdegree = weather.forecastList.get(0).temperature.maxtemperature +
                                    "/" + weather.forecastList.get(0).temperature.mintemperature + "℃";
                            new Thread() {
                                public void run() {
                                    Display display = new Display();
                                    display.setDistemperature(disdegree);
                                    display.updateAll("weatherId = ?", weatherId);
                                }
                            }.start();
                            loadPic(weather.now.situation.weathercode);
                            showWeatherInfo(weather);
                            mRecyclerView.setAdapter(fillDataToRecyclerView(mforecastList));
                        } else {
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查您的网络设置", Toast.LENGTH_SHORT).show();
                            Log.d("1", weather.status);
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
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败,请检查您的网络设置", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void loadPic(String weathercode) {
        code = Integer.parseInt(weathercode);
        if (code == 100) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_sunny).asBitmap().skipMemoryCache(true).into(PicImg);
        } else if (code >= 101 && code <= 103) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_cloudy).asBitmap().skipMemoryCache(true).into(PicImg);
        } else if (code == 104) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_overcast).asBitmap().skipMemoryCache(true).into(PicImg); // 阴天
        } else if (code >= 300 && code <= 313) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_rain).asBitmap().skipMemoryCache(true).into(PicImg);
        } else if (code >= 400 && code <= 407) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_snow).asBitmap().skipMemoryCache(true).into(PicImg);
        } else if (code == 500 || code == 501) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_fog).asBitmap().skipMemoryCache(true).into(PicImg);
        } else if (code == 502) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_haze).asBitmap().skipMemoryCache(true).into(PicImg);
        } else if (code >= 503 && code <= 508) {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_duststorm).asBitmap().skipMemoryCache(true).into(PicImg);
        } else {
            Glide.with(WeatherActivity.this).load(R.drawable.bg_normal).asBitmap().skipMemoryCache(true).into(PicImg);
        }
    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    private void showWeatherInfo(Weather weather) {
        weatherId = weather.basic.weatherId;
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";

        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weather.now.situation.weatherinfo);
        //服务器中港澳台没有AQI数据
        if (weather.aqi != null) {
            levelText.setText(weather.aqi.city.level);
        }
        //近期预报赋值
        mforecastList = weather.forecastList;

        Resources resources = this.getResources();
        Forecast forecast = weather.forecastList.get(0);
        String degree2 = forecast.temperature.maxtemperature + "/" + forecast.temperature.mintemperature + "℃";
        degree2Text.setText(degree2);
        weather2InfoText.setText(weather.now.situation.weatherinfo);
        int iconday = resources.getIdentifier("ic_" + forecast.situation.iconday, "drawable", this.getPackageName());
        if (iconday == 0) {
            weather2Image.setImageResource(R.drawable.ic_999);
        } else {
            weather2Image.setImageResource(iconday);
        }
        forecast = weather.forecastList.get(1);
        String degree3 = forecast.temperature.maxtemperature + "/" + forecast.temperature.mintemperature + "℃";
        degree3Text.setText(degree3);
        weather3InfoText.setText(forecast.situation.weatherinfo_d);
        int iconight = resources.getIdentifier("ic_" + forecast.situation.iconight, "drawable", this.getPackageName());
        if (iconight == 0) {
            weather3Image.setImageResource(R.drawable.ic_999);
        } else {
            weather3Image.setImageResource(iconight);
        }

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
        //weatherLayout.setVisibility(View.VISIBLE); // 暂时隐藏
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    private ForecastAdapter fillDataToRecyclerView(List<Forecast> forecasts) {
        int max[] = new int[NUM];
        int min[] = new int[NUM];
        int temp = 0;
        for (Forecast forecast : forecasts) {
            max[temp] = Integer.parseInt(forecast.temperature.maxtemperature);
            temp++;
        }
        temp = 0;
        for (Forecast forecast : forecasts) {
            min[temp] = Integer.parseInt(forecast.temperature.mintemperature);
            temp++;
        }
        int number = max[0];
        for (temp = 1; temp < max.length; temp++) {
            if (max[temp] > number) {
                number = max[temp];
            }
        }
        final int high = number;
        number = min[0];
        for (temp = 1; temp < min.length; temp++) {
            if (min[temp] < number) {
                number = min[temp];
            }
        }
        final int low = number;
        ForecastAdapter adapter = new ForecastAdapter(WeatherActivity.this, mforecastList, low, high);
        return adapter;
    }

}
