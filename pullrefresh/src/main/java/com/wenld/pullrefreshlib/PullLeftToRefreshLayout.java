package com.wenld.pullrefreshlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static com.wenld.pullrefreshlib.LoadViewCreator.PULL_TO_LOADMORE;
import static com.wenld.pullrefreshlib.LoadViewCreator.UP_TO_LOADMORE;

/**
 * <p/>
 * Author: 温利东 on 2017/2/27 10:23.
 * blog: http://blog.csdn.net/sinat_15877283
 * github: https://github.com/LidongWen
 * <p>
 *
 * @Description 左滑加载容器
 */

public class PullLeftToRefreshLayout extends FrameLayout {

    private String TAG = "PullLeftToRefreshLayout";
    //加载更多辅助类
    private LoadViewCreator loadViewCreator;


    private boolean isLeftLoading = false;  //是否正在加载
    private boolean isLeftLoadMore = true;// 是否可以被加载


    private View moreView;
    /**
     * moreView 的宽度
     */
    private int moreViewWidth = 0;
    /**
     * 弹回动画
     */
    private ValueAnimator mBackAnimator;
    /**
     * loadView的偏移量
     */
    private float offsetxLoadView = 0;


    protected View mChildView;//子布局
    protected boolean isTranslationChild = true;//是否位移子布局
    /**
     * view的偏移量
     */
    protected float offsetxChild = 0;
    protected static final long BACK_ANIM_DUR = 400; //回弹时间;


    private OnLeftLoadMoreListener onLoadMoreListener;

    public PullLeftToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullLeftToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLeftToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
                setRefreshViewMarginRight((int) val);
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

                    //判断 是否在左滑 && 子VIew是否还能左滑
                    if (dx < 0 && !canScrollLeft()) {
                        return true;
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLeftLoading()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (!isCanDrag())
                    return true;
                mTouchCurX = event.getX();

                float dx = mTouchCurX - mTouchStartX;
                if (moreView == null || dx > 0) {
                    return true;
                }
                int offsetxTouch = countDistance((int) dx, 0);

                setRefreshViewMarginRight(-offsetxTouch - moreViewWidth);

                if (loadViewCreator != null) {
                    if (offsetxLoadView > 0) {
                        loadViewCreator.onPull(offsetxTouch, moreViewWidth, UP_TO_LOADMORE);
                    } else {
                        loadViewCreator.onPull(offsetxTouch, moreViewWidth, PULL_TO_LOADMORE);
                    }
                }

                return true;
            case MotionEvent.ACTION_UP:
                mTouchStartX = 0;
                mTouchCurX = 0;
                if (isCanDrag()) {
                    if (offsetxLoadView > 0) {
                        isLeftLoading = true;
                        mBackAnimator.setFloatValues(offsetxLoadView, 0);
                        mBackAnimator.start();
                    } else {
                        //回弹到 -moreViewWidth
                        mBackAnimator.setFloatValues(offsetxLoadView, -moreViewWidth);
                        mBackAnimator.start();
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 是否可以被拖拽
     *
     * @return
     */
    private boolean isCanDrag() {
        return isLeftLoadMore && !isLeftLoading && moreView != null && mChildView != null;
    }

    protected boolean isLeftLoading() {
        return isLeftLoading;
    }

    private void addMoreView() {
        moreView = loadViewCreator.getLoadView(this.getContext(), this);
        moreViewWidth = moreView.getMeasuredWidth();

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.rightMargin = -moreViewWidth;
        moreView.setLayoutParams(params);

        addViewInternal(moreView);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (getChildCount() > 1) {
            mChildView = getChildAt(0);
        }
        //if (changed) {
        if (moreView != null && moreViewWidth <= 0) {
            // 获取头部刷新View的高度
            moreViewWidth = moreView.getMeasuredWidth();
            if (moreViewWidth > 0) {
                // 隐藏头部刷新的View
                setRefreshViewMarginRight(-moreViewWidth);
            }
        }
    }

    private class AnimListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            Log.e(TAG, "onAnimationEnd::" + isLeftLoading);
            if (isLeftLoading) {
                if (loadViewCreator != null) {
                    loadViewCreator.onLoading();
                }
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadmore();
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

    private boolean canScrollLeft() {
        if (mChildView == null) {
            return false;
        }
        return ViewCompat.canScrollHorizontally(mChildView, 1);
    }

    /**
     * 设置刷新View的rightMargin  以及 位移hild
     */
    private void setRefreshViewMarginRight(int marginRight) {
        proceeOffsetxChild(marginRight);
        MarginLayoutParams params = (MarginLayoutParams) moreView.getLayoutParams();
        params.rightMargin = (int) offsetxLoadView;
        moreView.setLayoutParams(params);

        if (isTranslationChild)
            mChildView.setTranslationX(offsetxChild);
    }

    private void proceeOffsetxChild(int marginRight) {
        if (marginRight < -moreViewWidth) {
            marginRight = -moreViewWidth;
        }
        offsetxLoadView = marginRight;
        offsetxChild = -offsetxLoadView - moreViewWidth;
    }

    private void addViewInternal(@NonNull View child) {
        this.addView(child);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    public void setLoadViewCreator(LoadViewCreator loadViewCreator) {
        if (moreView != null) {
            removeView(moreView);
        }
        this.loadViewCreator = loadViewCreator;
        addMoreView();
    }

    public void setLoadMoreLeft(boolean loadMore) {
        isLeftLoadMore = loadMore;
    }

    public void stopLoad() {
        if (!isLeftLoading)
            return;
        isLeftLoading = false;
        if (loadViewCreator != null) {
            loadViewCreator.onStopLoad();
        }
        mBackAnimator.setFloatValues(offsetxLoadView, -moreViewWidth);
        mBackAnimator.start();

    }

    public void setOnLeftLoadMoreListener(OnLeftLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setTranslationChild(boolean translationChild) {
        isTranslationChild = translationChild;
    }

    /**
     * 计算 弹性后的 偏移量
     *
     * @param dx
     * @param F  阻力值
     */
    protected int countDistance(int dx, float F) {
        return dx / 2;
    }

    /**
     * 计算 RefreshView的偏移量，以及视图的偏移量
     *
     * @param ontouchDistance 手指偏移量
     * @param defaultOffsetX  默认偏移量
     * @param direction       坐标方向
     * @return int[]
     */
    protected int[] proceeOffsetxChild(int ontouchDistance, int defaultOffsetX, int direction) {

//        if (ontouchDistance < defaultOffsetX) {
//            ontouchDistance = defaultOffsetX;
//        }
//        offsetxLoadView = ontouchDistance;
//        offsetxChild = direction*ontouchDistance +defaultOffsetX;

        int[] ints = new int[2];
        ints[0] = ontouchDistance;
        ints[1] = direction*ontouchDistance +defaultOffsetX;
        return ints;
    }
}
