package com.rainweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rainweather.android.gson.Forecast;
import com.rainweather.android.gson.Weather;
import com.rainweather.android.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class DetailWeatherActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int NUM = 7;

    private int code;

    private ImageView PicImg;

    private TextView detail_title_city;

    private Button backmainButton;

    private ViewPager viewPager;

    private FragmentPagerAdapter mAdpter;

    private List<Fragment> mFragments = new ArrayList<>();

    private ArrayList<TextView> Layout;

    private TextView Layout_1;

    private TextView Layout_2;

    private TextView Layout_3;

    private TextView Layout_4;

    private TextView Layout_5;

    private TextView Layout_6;

    private TextView Layout_7;

    // 滚动条图片
    private ImageView scrollbar;

    // 滚动条初始偏移量
    private int offset = 0;

    // 当前页编号
    private int currIndex = 0;

    // 滚动条宽度
    private int bmpW;

    //一倍滚动量
    private int one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_weather);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        SharedPreferences prefs = getSharedPreferences("data1", MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);
        Weather weather = Utility.handleWeatherResponse(weatherString);
        PicImg = (ImageView) findViewById(R.id.detail_pic_img);
        loadPic(weather.now.situation.weathercode);
        detail_title_city = (TextView) findViewById(R.id.detail_title_city);
        backmainButton = (Button) findViewById(R.id.detail_backmain_button);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        Layout_1 = (TextView) findViewById(R.id.Layout_1);
        Layout_2 = (TextView) findViewById(R.id.Layout_2);
        Layout_3 = (TextView) findViewById(R.id.Layout_3);
        Layout_4 = (TextView) findViewById(R.id.Layout_4);
        Layout_5 = (TextView) findViewById(R.id.Layout_5);
        Layout_6 = (TextView) findViewById(R.id.Layout_6);
        Layout_7 = (TextView) findViewById(R.id.Layout_7);
        scrollbar = (ImageView) findViewById(R.id.scrollbar);

        detail_title_city.setText(weather.basic.cityName);
        backmainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailWeatherActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Forecast forecast = weather.forecastList.get(0);
        String date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_1.setText(date);
        forecast = weather.forecastList.get(1);
        date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_2.setText(date);
        forecast = weather.forecastList.get(2);
        date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_3.setText(date);
        forecast = weather.forecastList.get(3);
        date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_4.setText(date);
        forecast = weather.forecastList.get(4);
        date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_5.setText(date);
        forecast = weather.forecastList.get(5);
        date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_6.setText(date);
        forecast = weather.forecastList.get(6);
        date = forecast.date;
        date = date.substring(date.length() - 5);
        Layout_7.setText(date);

        Layout_1.setOnClickListener(this);
        Layout_2.setOnClickListener(this);
        Layout_3.setOnClickListener(this);
        Layout_4.setOnClickListener(this);
        Layout_5.setOnClickListener(this);
        Layout_6.setOnClickListener(this);
        Layout_7.setOnClickListener(this);

        //Detail1Fragment tab01 = Detail1Fragment.newInstance(1);
        Detail1Fragment tab01 = new Detail1Fragment();
        Detail2Fragment tab02 = new Detail2Fragment();
        Detail3Fragment tab03 = new Detail3Fragment();
        Detail4Fragment tab04 = new Detail4Fragment();
        Detail5Fragment tab05 = new Detail5Fragment();
        Detail6Fragment tab06 = new Detail6Fragment();
        Detail7Fragment tab07 = new Detail7Fragment();
        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(tab03);
        mFragments.add(tab04);
        mFragments.add(tab05);
        mFragments.add(tab06);
        mFragments.add(tab07);
        //初始化Adapter这里使用FragmentPagerAdapter
        mAdpter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        //绑定适配器
        viewPager.setAdapter(mAdpter);
        //设置viewPager的初始界面为第一个界面
        int position = getIntent().getIntExtra("position", 1);
        viewPager.setCurrentItem(position);

        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / NUM - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = screenW / NUM;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);

        Animation animation = new TranslateAnimation(one * position, one * position, 0, 0);
        currIndex = position;
        animation.setFillAfter(true);
        animation.setDuration(200);
        scrollbar.startAnimation(animation);
    }

    private void loadPic(String weathercode) {
        code = Integer.parseInt(weathercode);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 100) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_sunny).asBitmap().into(PicImg);
                } else if (code >= 101 && code <=103) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_cloudy).asBitmap().into(PicImg);
                } else if (code == 104) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_overcast).asBitmap().into(PicImg);
                } else if (code >= 300 && code <= 313) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_rain).asBitmap().into(PicImg);
                } else if (code >= 400 && code <= 407) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_snow).asBitmap().into(PicImg);
                } else if (code == 500 || code == 501) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_fog).asBitmap().into(PicImg);
                } else if (code == 502) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_haze).asBitmap().into(PicImg);
                } else if (code >= 503 && code <= 508) {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_duststorm).asBitmap().into(PicImg);
                } else {
                    Glide.with(DetailWeatherActivity.this).load(R.drawable.bg_normal).asBitmap().into(PicImg);
                }
            }
        });
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(0, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(one, one, 0, 0);
                    break;
                case 2:
                    animation = new TranslateAnimation(one * 2, one * 2, 0, 0);
                    break;
                case 3:
                    animation = new TranslateAnimation(one * 3, one * 3, 0, 0);
                    break;
                case 4:
                    animation = new TranslateAnimation(one * 4, one * 4, 0, 0);
                    break;
                case 5:
                    animation = new TranslateAnimation(one * 5, one * 5, 0, 0);
                    break;
                case 6:
                    animation = new TranslateAnimation(one * 6, one * 6, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Layout_1:
                //切换到第一页
                viewPager.setCurrentItem(0);
                break;
            case R.id.Layout_2:
                viewPager.setCurrentItem(1);
                break;
            case R.id.Layout_3:
                viewPager.setCurrentItem(2);
                break;
            case R.id.Layout_4:
                viewPager.setCurrentItem(3);
                break;
            case R.id.Layout_5:
                viewPager.setCurrentItem(4);
                break;
            case R.id.Layout_6:
                viewPager.setCurrentItem(5);
                break;
            case R.id.Layout_7:
                viewPager.setCurrentItem(6);
                break;
        }
    }

    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(DetailWeatherActivity.this, WeatherActivity.class);
        startActivity(intent);
        finish();
    }

}
