package com.evport.businessapp.ui.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.evport.businessapp.R;

public class DashLineView extends View {

    Paint mPaint;
    private Path mPath;
    public DashLineView(Context context) {
        super(context);
        init();
    }

    public DashLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private  void  init(){
        mPaint = new Paint();
        mPath = new Path();

        mPaint.setStrokeWidth(3);
        mPaint.setPathEffect(new DashPathEffect(new float[] {15, 5}, 0));
        mPaint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //获取View的宽高
        int width = getWidth();
        int height = getHeight();


        int centerY = getHeight() / 2;
        mPath.reset();
        mPath.moveTo(0, centerY);
        mPath.lineTo(getWidth(), centerY);
        canvas.drawPath(mPath, mPaint);
//        canvas.drawRect(0, 0, width, height, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int colorStart = getResources().getColor(R.color.dash_line_1);
        int color1 = getResources().getColor(R.color.dash_line_2);
        int colorEnd = getResources().getColor(R.color.dash_line_3);
        LinearGradient backGradient = new LinearGradient(0, 0, w, 0, new int[]{colorStart, color1 ,colorEnd,colorStart}, null, Shader.TileMode.CLAMP);
        mPaint.setShader(backGradient);
    }
}