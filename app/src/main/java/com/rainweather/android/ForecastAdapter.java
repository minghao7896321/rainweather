package com.rainweather.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rainweather.android.gson.Forecast;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by lenovo on 2017/4/22.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private Context mContext;
    private List<Forecast> mForecastList;
    int mLowestTem;
    int mHighestTem;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View forecastView;
        TextView dateText;
        TextView dayText;
        ImageView dayIcon;
        WeatherLineView weatherLineView;
        ImageView nightIcon;
        TextView nightText;
        TextView directionText;
        TextView windpowerText;

        public ViewHolder(View view) {
            super(view);
            forecastView = view;
        }
    }

    public ForecastAdapter(Context context, List<Forecast> forecasts, int lowtem, int hightem) {
        mContext = context;
        mForecastList = forecasts;
        mLowestTem = lowtem;
        mHighestTem = hightem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.dateText = (TextView) view.findViewById(R.id.date_text);
        holder.dayText = (TextView) view.findViewById(R.id.weather_d);
        holder.dayIcon = (ImageView) view.findViewById(R.id.ic_day_weather);
        holder.weatherLineView = (WeatherLineView) view.findViewById(R.id.wea_line);
        holder.nightIcon = (ImageView) view.findViewById(R.id.ic_night_weather);
        holder.nightText = (TextView) view.findViewById(R.id.weather_n);
        holder.directionText = (TextView) view.findViewById(R.id.direction_text);
        holder.windpowerText = (TextView) view.findViewById(R.id.windpower_text);
        holder.forecastView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(v.getContext(), DetailWeatherActivity.class);
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
                // if(context instanceof Activity)同样效果
                if(Activity.class.isInstance(v.getContext()))
                {
                    // 转化为activity，然后finish就行了
                    Activity activity = (Activity)v.getContext();
                    activity.finish();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 最低温度设置为15，最高温度设置为30
        Resources resources = mContext.getResources();
        Forecast forecast = mForecastList.get(position);
        String date = forecast.date;
        date = date.substring(date.length() - 5);
        holder.dateText.setText(date);
        holder.dayText.setText(forecast.situation.weatherinfo_d);
        int iconday = resources.getIdentifier("ic_" + forecast.situation.iconday, "drawable", mContext.getPackageName());
        if (iconday == 0) {
            holder.dayIcon.setImageResource(R.drawable.ic_999);
        } else {
            holder.dayIcon.setImageResource(iconday);
        }

        holder.weatherLineView.setLowHighestData(mLowestTem, mHighestTem);

        int iconight = resources.getIdentifier("ic_" + forecast.situation.iconight + "_n", "drawable", mContext.getPackageName());
        if (iconight == 0) {
            holder.nightIcon.setImageResource(R.drawable.ic_999);
        } else {
            holder.nightIcon.setImageResource(iconight);
        }
        holder.nightText.setText(forecast.situation.weatherinfo_n);
        if (forecast.wind.direction.equals("无持续风向")) { // 如果比13小会导致forecast_item不一边高
            holder.directionText.setTextSize(13);
        }
        holder.directionText.setText(forecast.wind.direction);
        holder.windpowerText.setText(forecast.wind.windpower + "级");

        int low[] = new int[3];
        int high[] = new int[3];
        try {
            low[1] = Integer.parseInt(forecast.temperature.mintemperature);
            high[1] = Integer.parseInt(forecast.temperature.maxtemperature);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (position <= 0) {
            low[0] = 0;
            high[0] = 0;
        } else {
            Forecast forecastLeft = mForecastList.get(position - 1);
            try {
                low[0] = (Integer.parseInt(forecastLeft.temperature.mintemperature) + Integer.parseInt(forecast.temperature.mintemperature)) / 2;
                high[0] = (Integer.parseInt(forecastLeft.temperature.maxtemperature) + Integer.parseInt(forecast.temperature.maxtemperature)) / 2;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (position >= mForecastList.size() - 1) {
            low[2] = 0;
            high[2] = 0;
        } else {
            Forecast forecastRight = mForecastList.get(position + 1);
            try {
                low[2] = (Integer.parseInt(forecast.temperature.mintemperature) + Integer.parseInt(forecastRight.temperature.mintemperature)) / 2;
                high[2] = (Integer.parseInt(forecast.temperature.maxtemperature) + Integer.parseInt(forecastRight.temperature.maxtemperature)) / 2;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        holder.weatherLineView.setLowHighData(low, high);
    }

    @Override
    public int getItemCount() {
        return mForecastList.size();
    }

}
