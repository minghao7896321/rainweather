package com.rainweather.android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.rainweather.android.db.Display;
import com.rainweather.android.draw.ManageItemDecoration;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/4/13.
 */

public class ManageAreaActivity extends AppCompatActivity {

    private List<Display> displayList = new ArrayList<>();

    private RecyclerView recyclerView;

    private Button backmainButton;

    private Button deleteButton;

    private Button doneButton;

    private Button addButton;

    private String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_area);
        backmainButton = (Button) findViewById(R.id.backmain_button);
        deleteButton = (Button) findViewById(R.id.delete_button);
        doneButton = (Button) findViewById(R.id.done_button);
        addButton = (Button) findViewById(R.id.add_button);
        displayList = DataSupport.findAll(Display.class);
        weatherId = getIntent().getStringExtra("now_weather_id");
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ManageAreaActivity.this);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.manage_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ManageItemDecoration());//添加装饰类
        ManageAdapter adapter = new ManageAdapter(displayList);
        recyclerView.setAdapter(adapter);

        backmainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Display> displays = DataSupport.where("weatherId = ?", weatherId).find(Display.class);
                /*String text = "";
                for (Display olddisplay : displays) {
                    text = olddisplay.getWeatherId();
                }*/
                //if (text.equals("")) {
                //删除当前主页面的城市时的操作方法
                if (displays.isEmpty()) {
                    Intent intent = new Intent(ManageAreaActivity.this, WeatherActivity.class);
                    weatherId = displayList.get(displayList.size() - 1).getWeatherId();
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(ManageAreaActivity.this, WeatherActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                int num = 1;
                for (Display display : displayList) {
                    num++;
                }*/
                if (displayList.size() > 1) {
                    deleteButton.setVisibility(View.GONE); // 设置控件以不占空间的形式隐藏
                    doneButton.setVisibility(View.VISIBLE);
                    backmainButton.setVisibility(View.INVISIBLE);
                    addButton.setVisibility(View.INVISIBLE);
                    DelAdapter delAdapter = new DelAdapter(displayList);
                    recyclerView.setAdapter(delAdapter);
                } else {
                    dialog.setTitle("删除城市");
                    dialog.setMessage("最少保留一个城市");
                    dialog.show();
                }
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
                backmainButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                ManageAdapter adapter = new ManageAdapter(displayList);
                recyclerView.setAdapter(adapter);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //最多九个地区
                /*
                int num = 1;
                for (Display display : displayList) {
                    num++;
                }*/
                if (displayList.size() < 9) {
                    Intent intent = new Intent(ManageAreaActivity.this, ChooseAreaActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    dialog.setTitle("添加城市");
                    dialog.setMessage("最多只能添加9个城市");
                    dialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doneButton.getVisibility() == View.VISIBLE) {
            deleteButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.GONE);
            backmainButton.setVisibility(View.VISIBLE);
            addButton.setVisibility(View.VISIBLE);
            ManageAdapter adapter = new ManageAdapter(displayList);
            recyclerView.setAdapter(adapter);
        } else {
            //super.onBackPressed();
            List<Display> displays = DataSupport.where("weatherId = ?", weatherId).find(Display.class);
            if (displays.isEmpty()) {
                Intent intent = new Intent(ManageAreaActivity.this, WeatherActivity.class);
                weatherId = displayList.get(displayList.size() - 1).getWeatherId();
                intent.putExtra("weather_id", weatherId);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(ManageAreaActivity.this, WeatherActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

}
