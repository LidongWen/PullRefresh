package com.wenld.pullrefreshlib;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p/>
 * Author: 温利东 on 2017/2/27 10:23.
 * blog: http://blog.csdn.net/sinat_15877283
 * github: https://github.com/LidongWen
 * 上拉加载更多的辅助类为了匹配所有效果
 */
public abstract class LoadViewCreator {
    //拉获取更多
    public static int PULL_TO_LOADMORE=2;
    //释放进行加载
    public static int UP_TO_LOADMORE=1;

    /**
     * 获取上拉加载更多的View
     *
     * @param context 上下文
     * @param parent  RecyclerView
     */
    public abstract View getLoadView(Context context, ViewGroup parent);

    /**
     * 正在上拉
     *
     * @param currentDragHeight 当前拖动的高度
     * @param loadViewHeight    总的加载高度
     * @param currentLoadStatus 当前状态
     */
    public abstract void onPull(int currentDragHeight, int loadViewHeight, int currentLoadStatus);

    /**
     * 正在加载中
     */
    public abstract void onLoading();

    /**
     * 停止加载
     */
    public abstract void onStopLoad();
}
