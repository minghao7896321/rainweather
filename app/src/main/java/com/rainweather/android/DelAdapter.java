package com.rainweather.android;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rainweather.android.db.Display;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by lenovo on 2017/4/21.
 */

public class DelAdapter extends RecyclerView.Adapter<DelAdapter.ViewHolder> {

    private List<Display> mDisplayList;

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

    public DelAdapter(List<Display> displays) {
        mDisplayList = displays;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.deleteItemButton.setVisibility(View.VISIBLE);
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
