package com.wenld.pullrefreshlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import static com.wenld.pullrefreshlib.LoadViewCreator.PULL_TO_LOADMORE;
import static com.wenld.pullrefreshlib.LoadViewCreator.UP_TO_LOADMORE;

/**
 * <p/>
 * Author: 温利东 on 2017/2/27 10:23.
 * blog: http://blog.csdn.net/sinat_15877283
 * github: https://github.com/LidongWen
 * <p>
 *
 * @Description 水平加载容器
 */

public class PullHorizontalRefreshLayout extends PullLeftToRefreshLayout {

    private String TAG = "PullLeftToRefreshLayout";
    //加载更多辅助类
    private RefreshViewCreator refreshViewCreator;

    private boolean isRightRefreshing = false;  //是否正在加载
    private boolean isRightRefresh = true;// 是否可以被加载

    private View refreshView;
    /**
     * refreshView 的宽度
     */
    private int refreshViewWidth;
    /**
     * loadView的偏移量
     */
    private float refreshViewOffsetX;
    /**
     * 弹回动画
     */
    private ValueAnimator mBackAnimator;

    private OnRightRefreshListener onRightRefreshListener;

    public PullHorizontalRefreshLayout(Context context) {
        this(context, null);
    }

    public PullHorizontalRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullHorizontalRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mBackAnimator = ValueAnimator.ofFloat(0, 0);
        mBackAnimator.addListener(new AnimListener());
        mBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                setRefreshViewMarginLeft((int) val);
            }
        });
        mBackAnimator.setDuration(BACK_ANIM_DUR);
    }

    private float mTouchStartX;  //第一次触发滑动的位置
    private float mTouchCurX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isCanDrag()) {
                    float curX = ev.getX();
                    float dx = curX - mTouchStartX;
                    mTouchStartX = ev.getX();
                    mTouchCurX = mTouchStartX;

                    //判断 是否在右滑 && 子VIew是否还能右滑
                    if (dx > 0 && !canScrollRight()) {
                        isTouch = true;
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isRightRefreshing() || isLeftLoading()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isCanDrag() && isTouch) {
                    mTouchCurX = event.getX();
                    float dx = mTouchCurX - mTouchStartX;
                    if (refreshView == null || dx < 0) {
                        return true;
                    }
                    int offsetxTouch = countDistance((int) dx, 0);
                    //刷新布局
                    setRefreshViewMarginLeft(offsetxTouch - refreshViewWidth);

                    if (refreshViewCreator != null) {
                        if (refreshViewOffsetX > 0) {
                            refreshViewCreator.onPull(offsetxTouch, refreshViewWidth, UP_TO_LOADMORE);
                        } else {
                            refreshViewCreator.onPull(offsetxTouch, refreshViewWidth, PULL_TO_LOADMORE);
                        }
                    }
                    return true;
                } else {
                    return super.onTouchEvent(event);
                }
            case MotionEvent.ACTION_UP:
                mTouchStartX = 0;
                mTouchCurX = 0;
                if (isCanDrag() && offsetxChild > 0 && isTouch) {
                    isTouch = false;
                    if (refreshViewOffsetX > 0) {
                        isRightRefreshing = true;
                        mBackAnimator.setFloatValues(refreshViewOffsetX, 0);
                        mBackAnimator.start();
                    } else {
                        //回弹到 -refreshViewWidth
                        mBackAnimator.setFloatValues(refreshViewOffsetX, -refreshViewWidth);
                        mBackAnimator.start();
                    }
                    return true;
                }
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouch = false;

    /**
     * 是否可以被拖拽
     *
     * @return
     */
    private boolean isCanDrag() {
        return isRightRefresh && !isRightRefreshing() && refreshView != null && mChildView != null && !isLeftLoading();
    }

    public boolean isRightRefreshing() {
        return isRightRefreshing;
    }

    private void addRefreshView() {
        refreshView = refreshViewCreator.getRefreshView(this.getContext(), this);
        refreshViewWidth = refreshView.getMeasuredWidth();

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        params.leftMargin = -refreshViewWidth;
        refreshView.setLayoutParams(params);

        super.addView(refreshView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //if (changed) {
        if (refreshView != null && refreshViewWidth <= 0) {
            // 获取头部刷新View的高度
            refreshViewWidth = refreshView.getMeasuredWidth();
            if (refreshViewWidth > 0) {
                // 隐藏头部刷新的View
                setRefreshViewMarginLeft(-refreshViewWidth);
            }
        }
    }

    private class AnimListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (isRightRefreshing) {
                if (refreshViewCreator != null) {
                    refreshViewCreator.onRefreshing();
                }
                if (onRightRefreshListener != null) {
                    onRightRefreshListener.onRefresh();
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }

    private boolean canScrollRight() {
        if (mChildView == null) {
            return false;
        }
        return ViewCompat.canScrollHorizontally(mChildView, -1);
    }

    /**
     * 设置刷新View的rightMargin  以及 位移hild
     */
    private void setRefreshViewMarginLeft(int marginRight) {

        proceeOffsetxChild(marginRight);

        MarginLayoutParams params = (MarginLayoutParams) refreshView.getLayoutParams();
        params.leftMargin = (int) refreshViewOffsetX;
        refreshView.setLayoutParams(params);

        if (isTranslationChild)
            mChildView.setTranslationX(offsetxChild);
    }

    private void proceeOffsetxChild(int marginRight) {
        if (marginRight < -refreshViewWidth) {
            marginRight = -refreshViewWidth;
        }
        refreshViewOffsetX = marginRight;
        offsetxChild = refreshViewWidth + refreshViewOffsetX;
    }

    public void setRefreshViewCreator(RefreshViewCreator refreshViewCreator) {
        if (refreshView != null) {
            removeView(refreshView);
        }
        this.refreshViewCreator = refreshViewCreator;
        addRefreshView();
    }

    public boolean isRefresh() {
        return isRightRefresh;
    }

    public void setRightRefresh(boolean rightRefresh) {
        isRightRefresh = rightRefresh;
    }

    public void stopRefresh() {
        if (!isRightRefreshing())
            return;
        isRightRefreshing = false;
        if (refreshViewCreator != null) {
            refreshViewCreator.onStopRefresh();
        }
        mBackAnimator.setFloatValues(refreshViewOffsetX, -refreshViewWidth);
        mBackAnimator.start();
    }

    public void setOnRightRefreshListener(OnRightRefreshListener onRightRefreshListener) {
        this.onRightRefreshListener = onRightRefreshListener;
    }
}
