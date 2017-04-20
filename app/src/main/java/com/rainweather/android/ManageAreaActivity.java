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

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/4/13.
 */

public class ManageAreaActivity extends AppCompatActivity {

    private List<Display> displayList = new ArrayList<>();

    private Button backmainButton;

    private Button deleteButton;

    private Button doneButton;

    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_area);
        backmainButton = (Button) findViewById(R.id.backmain_button);
        deleteButton = (Button) findViewById(R.id.delete_button);
        doneButton = (Button) findViewById(R.id.done_button);
        addButton = (Button) findViewById(R.id.add_button);
        displayList =  DataSupport.findAll(Display.class);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ManageAreaActivity.this);
        dialog.setTitle("添加城市");
        dialog.setMessage("最多只能添加9个城市");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //添加装饰类
        recyclerView.addItemDecoration(new MyItemDecoration());
        ManageAdapter adapter = new ManageAdapter(displayList);
        recyclerView.setAdapter(adapter);
        backmainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageAreaActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButton.setVisibility(View.GONE); // 设置控件以不占空间的形式隐藏
                doneButton.setVisibility(View.VISIBLE);
                backmainButton.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.INVISIBLE);
                Intent intent = new Intent("delete");
                sendBroadcast(intent); // 发送标准广播
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneButton.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
                backmainButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                Intent intent = new Intent("done");
                sendBroadcast(intent); // 发送标准广播
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //最多九个地区
                int num = 1;
                for (Display display : displayList) {
                    num++;
                }
                if (num <= 9) {
                    Intent intent = new Intent(ManageAreaActivity.this, ChooseAreaActivity.class);
                    startActivity(intent);
                } else {
                    dialog.show();
                }
            }
        });
    }

}
