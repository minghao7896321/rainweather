package com.rainweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.rainweather.android.db.Display;
import com.rainweather.android.gson.Weather;
import com.rainweather.android.util.HttpUtil;
import com.rainweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 6 * 60 * 60 * 1000; //6小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        //从系统开机时间算起，加上延迟执行的时间
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        SharedPreferences prefs = getSharedPreferences("data1", MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String mweatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + mweatherId + "&key=af71e684a8f540feaaad7c92566324c1";
            //String weatherUrl = "http://guolin.tech/api/weather?cityid=" + mweatherId + "&key=c0dcdf9494e24fa39deeadbe424c8394";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = getSharedPreferences("data1", MODE_PRIVATE).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        final String weatherId = weather.basic.weatherId;
                        final String disdegree = weather.forecastList.get(0).temperature.maxtemperature + "/" + weather.forecastList.get(0).temperature.mintemperature + "℃";
                        new Thread() {
                            public void run() {
                                Display display = new Display();
                                display.setDistemperature(disdegree);
                                display.updateAll("weatherId = ?", weatherId);
                            }
                        }.start();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(AutoUpdateService.this, "获取天气信息失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
