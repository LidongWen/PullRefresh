package com.wenld.pullrefreshlib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import static com.wenld.pullrefreshlib.R.id.tvMoreText;

/**
 * <p/>
 * Author: 温利东 on 2017/2/27 10:23.
 * blog: http://blog.csdn.net/sinat_15877283
 * github: https://github.com/LidongWen
 * 默认加载器
 */

public class DefaultLoadCreator extends LoadViewCreator {
    // 加载数据的ImageView
    private TextView moreText;
    private ImageView arrowIv;

    private int currentRefreshStatus;

    public DefaultLoadCreator() {
        initRotateAnim();
    }

    private void initRotateAnim() {
    }

    @Override
    public View getLoadView(Context context, ViewGroup parent) {
        View moreView = LayoutInflater.from(context).inflate(R.layout.item_load_more, parent, false);
        moreText = (TextView) moreView.findViewById(tvMoreText);
        arrowIv = (ImageView) moreView.findViewById(R.id.ivRefreshArrow);
        return moreView;
    }

    @Override
    public void onPull(int currentDragHeight, int refreshViewHeight, int currentRefreshStatus) {
        float rotate = ((float) currentDragHeight) / refreshViewHeight;
        // 不断下拉的过程中旋转图片
        arrowIv.setRotation(rotate / 360);

        if (this.currentRefreshStatus == currentRefreshStatus)
            return;
        this.currentRefreshStatus = currentRefreshStatus;

        if (currentRefreshStatus == PULL_TO_LOADMORE) {
            moreText.setText("上拉加载更多");
        }
        if (currentRefreshStatus == UP_TO_LOADMORE) {
            moreText.setText("松开加载更多");
        }
    }

    @Override
    public void onLoading() {
        moreText.setText("正在加载数据");
        if (animation != null) {
            animation.cancel();
        }
        // 刷新的时候不断旋转
        animation = new RotateAnimation(0, 720,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        arrowIv.startAnimation(animation);
    }

    RotateAnimation animation;

    @Override
    public void onStopLoad() {
        // 停止加载的时候清除动画
        arrowIv.clearAnimation();
        if (animation != null) {
            animation = null;
        }
        arrowIv.setRotation(0);
    }
}
