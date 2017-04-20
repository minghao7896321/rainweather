package com.rainweather.android;

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

    public static final int DELETE = 0;

    public static final int DONE = 1;

    public static final int DIS = 2;

    public static int move = DIS;

    public static int i = 1;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View manageView;
        Button deleteItemButton;
        TextView cityName;
        TextView degree_info;

        public ViewHolder(View view) {
            super(view);
            manageView = view;
            deleteItemButton = (Button) view.findViewById(R.id.delete_item_button);
            cityName = (TextView) view.findViewById(R.id.discity);
            degree_info = (TextView) view.findViewById(R.id.disdegree_info_text);
        }
    }

    public ManageAdapter(List<Display> displays) {
        mDisplayList = displays;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_item, parent, false);
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
            }
        });
        holder.deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Display display = mDisplayList.get(position);
                String weatherId = display.getWeatherId();
                mDisplayList.remove(position);
                notifyItemRemoved(position);
                DataSupport.deleteAll(Display.class, "weatherId = ?", weatherId);
                /*if (weatherId == 当前WeatherActivity的Id（已经发给ManageAreaActivity，是否应该发给ManageArea....？毕竟有final）)
                {
                    for (Display lastDisplay : mDisplayList) {
                        String lastweatherId = lastDisplay.getWeatherId();
                    }
                    发一个广播给WeatherActivity
                }*/
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Display display = mDisplayList.get(position);
        holder.cityName.setText(display.getCountyName());
        holder.degree_info.setText(display.getDistemperature());
        /**
         * 因为RecyclerViewAdapter正在更新RecyclerView,也就是调用OnBindViewHolder的时候我们又来调用Notifydatasetchanged方法
         * 这个方法也是刷新界面，最终肯定也是调用OnBindViewHolder,同时调用自然会抛出异常
         * 涉及到线程问题，那就涉及到同步跟异步的问题。
         * 解决方法，使用handler类排队，等待recyclerview 更新结束之后再刷新。
         */
        if (move == DELETE) {
            holder.deleteItemButton.setVisibility(View.VISIBLE);
            Log.d("manage", "1");
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        Log.d("manage", "2");
                    }
                }, 300);*/
            i++;
            if (i > 3) move = DIS;
        }
        if (move == DONE) {
            holder.deleteItemButton.setVisibility(View.INVISIBLE);
            Log.d("manage", "2");
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                        Log.d("manage", "2");
                    }
                }, 300);*/
            i++;
            if (i > 3) move = DIS;
        }
        if (move == DIS) {
            Log.d("manage", "3");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();// 如何减少刷新次数？
                    Log.d("manage", "4");
                }
            }, 300);
            i = 1;
        }
    }

    @Override
    public int getItemCount() {
        return mDisplayList.size();
    }

    public static class DeleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            move = DELETE;
            i = 1;
            Log.d("Manage", "DELETE");
        }
    }

    public static class DoneReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            move = DONE;
            i = 1;
            Log.d("Manage", "DONE");
        }
    }

}
