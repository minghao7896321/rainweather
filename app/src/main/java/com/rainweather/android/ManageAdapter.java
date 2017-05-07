package com.rainweather.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rainweather.android.db.Display;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by lenovo on 2017/4/15.
 */

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ViewHolder> {

    private List<Display> mDisplayList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View manageView;
        TextView cityName;
        TextView degree_info;

        public ViewHolder(View view) {
            super(view);
            manageView = view;
            cityName = (TextView) view.findViewById(R.id.discity);
            degree_info = (TextView) view.findViewById(R.id.disdegree_info_text);
        }
    }

    public ManageAdapter(List<Display> displays) {
        mDisplayList = displays;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.manageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Display display = mDisplayList.get(position);
                String weatherId = display.getWeatherId();
                Intent intent = new Intent(v.getContext(), WeatherActivity.class);
                intent.putExtra("weather_id", weatherId);
                v.getContext().startActivity(intent);
                // if(context instanceof Activity)同样效果
                if(Activity.class.isInstance(v.getContext())) // 有点丢帧？
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
        Display display = mDisplayList.get(position);
        holder.cityName.setText(display.getCountyName());
        holder.degree_info.setText(display.getDistemperature());
    }

    @Override
    public int getItemCount() {
        return mDisplayList.size();
    }

}
