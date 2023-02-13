package com.evport.businessapp.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.badoo.mobile.util.WeakHandler;
import com.evport.businessapp.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BubbleView extends View {
    private static final String TAG = BubbleView.class.getSimpleName();
    private HandlerThread handlerThread;
    private WeakHandler weakHandler;
    private static final int MESSAGE_UPDATE = 1;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private int bubbleNumLimit;
    private float minBubbleSpeedX;
    private float maxBubbleSpeedX;
    private float minBubbleSpeedY;
    private float maxBubbleSpeedY;
    private float minBubbleRadius;
    private float maxBubbleRadius;
    private float bubbleCreateLatch;
    private int width;
    private int height;
    private int defaultWidth = (int) dp2px(10);
    private int defaultHeight = (int) dp2px(20);

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleView);
        minBubbleSpeedX = ta.getDimensionPixelSize(R.styleable.BubbleView_bvMinBubbleSpeedX, (int) dp2px(1f));
        maxBubbleSpeedX = ta.getDimensionPixelSize(R.styleable.BubbleView_bvMaxBubbleSpeedX, (int) dp2px(2));
        minBubbleSpeedY = ta.getDimensionPixelSize(R.styleable.BubbleView_bvMinBubbleSpeedY, (int) dp2px(1));
        maxBubbleSpeedY = ta.getDimensionPixelSize(R.styleable.BubbleView_bvMaxBubbleSpeedY, (int) dp2px(4));
        minBubbleRadius = ta.getDimensionPixelSize(R.styleable.BubbleView_bvMinBubbleRadius, (int) dp2px(2));
        maxBubbleRadius = ta.getDimensionPixelSize(R.styleable.BubbleView_bvMaxBubbleRadius, (int) dp2px(8));
        bubbleNumLimit = ta.getInt(R.styleable.BubbleView_bvBubbleNumLimit, 15);
        bubbleCreateLatch = ta.getFloat(R.styleable.BubbleView_bvBubbleCreateLatch, 0.5f);
        int color = ta.getColor(R.styleable.BubbleView_bvBubbleColor, getResources().getColor(R.color.colorTheme));
        int alpha = ta.getInt(R.styleable.BubbleView_bvBubbleAlpha, 128);
        ta.recycle();
//        paint.setColor(color);
        paint.setAlpha(alpha);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        handlerThread = new HandlerThread(TAG);
//        handlerThread.start();
//        Looper looper = handlerThread.getLooper();
//        weakHandler = new WeakHandler(looper, msg -> {
//            if (msg.what == MESSAGE_UPDATE) {
//                tryCreateBubbles();
//                updateBubbles();
//                postInvalidate();
//            }
//            return true;
//        });

    }


//    public void  startBubble(){
//        if (handlerThread == null||weakHandler == null) {
//            handlerThread = new HandlerThread(TAG);
//            handlerThread.start();
//            Looper looper = handlerThread.getLooper();
//            weakHandler = new WeakHandler(looper, msg -> {
//                if (msg.what == MESSAGE_UPDATE) {
//                    tryCreateBubbles();
//                    updateBubbles();
//                    postInvalidate();
//                }
//                return true;
//            });
//        }else {
//            weakHandler.sendEmptyMessage(MESSAGE_UPDATE);
//        }
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        weakHandler.removeCallbacksAndMessages(null);
//        handlerThread.quit();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth() - getPaddingLeft() - getPaddingRight();
        height = getHeight() - getPaddingTop() - getPaddingBottom();

        @SuppressLint("DrawAllocation") LinearGradient lg=new LinearGradient(0,0,width / 2f,height+2,getResources().getColor(R.color.bubble_blue),getResources().getColor(R.color.bubble_green),Shader.TileMode.MIRROR);
        paint.setShader(lg);
        drawBubbles(canvas);

//        weakHandler.sendEmptyMessage(MESSAGE_UPDATE);
        tryCreateBubbles();
        updateBubbles();
        postInvalidate();
    }

    private void drawBubbles(Canvas canvas) {
        for (Bubble bubble : bubbles) {

            canvas.drawCircle(bubble.getCenterX(), bubble.getCenterY(), bubble.getRadius(), paint);
        }
    }

    private void updateBubbles() {
        Iterator<Bubble> iterator = bubbles.iterator();
        while (iterator.hasNext()) {
            Bubble bubble = iterator.next();
            bubble.setCenterX(bubble.getCenterX() + bubble.getSpeedX());
            bubble.setCenterY(bubble.getCenterY() - bubble.getSpeedY());
            if (isBubbleTouchTop(bubble)) {
                iterator.remove();
            } else if (isBubbleTouchLeft(bubble)) {
                bubble.setSpeedX(-bubble.getSpeedX());
                bubble.setCenterX(bubble.getRadius());
            } else if (isBubbleTouchRight(bubble)) {
                bubble.setSpeedX(-bubble.getSpeedX());
                bubble.setCenterX(width - bubble.getRadius());
            }
        }
    }

    private boolean isBubbleTouchLeft(Bubble bubble) {
        return bubble.getCenterX() - bubble.getRadius() <= 0;
    }

    private boolean isBubbleTouchRight(Bubble bubble) {
        return bubble.getCenterX() + bubble.getRadius() >= width;
    }

    private boolean isBubbleTouchTop(Bubble bubble) {
        return bubble.getCenterY() - bubble.getRadius() <= 0;
    }

    private List<Bubble> bubbles = new ArrayList<>();

    private void tryCreateBubbles() {
        if (bubbles.size() >= bubbleNumLimit) {
            return;
        }
        if (random.nextFloat() < bubbleCreateLatch) {
            return;
        }
        bubbles.add(createBubble());
    }

    private Bubble createBubble() {
        Bubble bubble = new Bubble();
        bubble.setRadius(getRandomRadius());
        bubble.setSpeedX(getRandomSpeedX());
        bubble.setSpeedY(getRandomSpeedY());
        bubble.setCenterX(width / 2f);
        bubble.setCenterY(height + bubble.getRadius());
        return bubble;
    }

    private Random random = new Random();

    private float getRandomSpeedX() {
        float value = minBubbleSpeedX + random.nextFloat() * (maxBubbleSpeedX - minBubbleSpeedX);
        if (random.nextBoolean()) {
            return value;
        } else {
            return -value;
        }
    }

    private float getRandomSpeedY() {
        return minBubbleSpeedY + random.nextFloat() * (maxBubbleSpeedY - minBubbleSpeedY);
    }

    private float getRandomRadius() {
        return minBubbleRadius + random.nextFloat() * (maxBubbleRadius - minBubbleRadius);
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = defaultWidth;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result;
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = defaultHeight;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 气泡
     */
    class Bubble {
        /**
         * 气泡的半径
         */
        private float radius;
        /**
         * 气泡 x 向的移动速度
         */
        private float speedX;
        /**
         * 气泡 y 向的移动速度
         */
        private float speedY;
        /**
         * 气泡的中心 x 坐标
         */
        private float centerX;
        /**
         * 气泡的中心 y 坐标
         */
        private float centerY;

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public float getSpeedX() {
            return speedX;
        }

        public void setSpeedX(float speedX) {
            this.speedX = speedX;
        }

        public float getSpeedY() {
            return speedY;
        }

        public void setSpeedY(float speedY) {
            this.speedY = speedY;
        }

        public float getCenterX() {
            return centerX;
        }

        public void setCenterX(float centerX) {
            this.centerX = centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public void setCenterY(float centerY) {
            this.centerY = centerY;
        }
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
