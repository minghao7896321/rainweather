package com.rainweather.android.draw;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lenovo on 2017/5/10.
 */

public class DetailItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * @param outRect 边界
     * @param view    recyclerView ItemView
     * @param parent  recyclerView
     * @param state   recycler 内部数据管理
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //设定底部边距为1px
        //四个参数分别是left,top,right,bottom
        outRect.set(0, 0, 1, 0);
    }
}