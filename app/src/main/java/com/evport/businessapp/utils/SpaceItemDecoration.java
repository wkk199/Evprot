package com.evport.businessapp.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int color;
    private int orientation;
    private int ms;
    private int me;
    private int type;
    private boolean isReverse;
    private Paint paint;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_REVERSE = 1;
    public static final int TYPE_ALL = 2;
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    public SpaceItemDecoration(int space, @ColorInt int color, int orientation) {
        this.space = space;
        this.color = color;
        this.orientation = orientation;
        initPaint();
    }

    public SpaceItemDecoration(int space, @ColorInt int color, int orientation, int ms, int me) {
        this.space = space;
        this.color = color;
        this.orientation = orientation;
        this.ms = ms;
        this.me = me;
        initPaint();
    }

    public SpaceItemDecoration(int space, @ColorInt int color, int orientation, int ms, int me, int type) {
        this.space = space;
        this.color = color;
        this.orientation = orientation;
        this.ms = ms;
        this.me = me;
        this.type = type;
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (orientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, space);
        } else {
            outRect.set(0, 0, space, 0);
        }

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        paint.setColor(color);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        //  -1 的目的是去掉最后一条分割线
        int count;
        int start;
        if (type == TYPE_REVERSE) {//当反转时，最后一条在顶部，第一条在底部，那么要不画第一条
            count = childCount;
            start = 1;
        } else if (type == TYPE_ALL) {//画所有
            count = childCount;
            start = 0;
        } else {//没反转时，不画最后一条
            start = 0;
            count = childCount - 1;
        }

        for (int i = start; i < count; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + space;
            c.drawRect(left + ms, top, right - me, bottom, paint);
        }
    }
}