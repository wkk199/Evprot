package com.evport.businessapp.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class CustomScrollView extends NestedScrollView {

    public CustomScrollView(@NonNull Context context) {
        super(context);
    }
    private float maxSlideDis;//向上滑动的最大滑动距离，没有超过这个距离时，拦截并处理掉向上滑动的事件
    //在activity或fragment中，根据布局参数进行设置
    private float mDownY;
    private float mSlop;
    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    public void setMaxSlideDis(float maxSlideDis) {
        this.maxSlideDis = maxSlideDis;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
       // Logger.log("CustomScrollView onInterceptTouchEvent " + ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dis = ev.getRawY() - mDownY;
                if (dis < 0 && Math.abs(dis) >= mSlop) {
                    //当触摸事件是向上滑动并且滑动距离超过屏幕的最小滑动单位时
                    return needScrollParent();
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    //scroller 是否已经滑动到了最高点
    public boolean needScrollParent() {
      //  Logger.log("CustomScrollView maxSlideDis = " + maxSlideDis + " getScrollY =" + getScrollY());
        return getScrollY() < maxSlideDis;
    }
    public CustomScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}