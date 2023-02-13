package com.evport.businessapp.ui.view;//package com.powercore.eviecharge.eviechargeljx.ui.view;
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.DashPathEffect;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PathEffect;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//
//import com.powercore.eviecharge.eviechargeljx.R;
//
//public class DottedLineView extends View {
//
//    private Paint mPaint;
//    private int mWidth;
//    private Path mPath;
//    private int mLineColor;
//    /**
//     * 虚线
//     */
//    private float mLineStrokeHeight;
//    /**
//     * 每格虚线的宽
//     */
//    private int mDottedLineWidth;
//
//    public DottedLineView(Context context) {
//        this(context, null);
//    }
//
//    public DottedLineView(Context context, @Nullable AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public DottedLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//        init(attrs);
//        initStyle();
//    }
//
//    private void init(AttributeSet attrs) {
//        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.DottedLineView);
//        mLineColor = array.getColor(R.styleable.DottedLineView_line_color, Color.BLACK);
//        mLineStrokeHeight = array.getDimension(R.styleable.DottedLineView_line_stroke_height, dp2px(getContext(), 1));
//        mDottedLineWidth = (int) array.getDimension(R.styleable.DottedLineView_dotted_line_width, 20);
//        array.recycle();
//    }
//
//    private void initStyle() {
//        mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setStrokeWidth(mLineStrokeHeight);
//        mPaint.setColor(mLineColor);
//        PathEffect pathEffect = new DashPathEffect(new float[]{mDottedLineWidth, mDottedLineWidth}, 1);
//        mPaint.setPathEffect(pathEffect);
//        mPath = new Path();
//    }
//
//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        mWidth = w;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        mPath.moveTo(0, 0);
//        mPath.lineTo(mWidth, 0);
//        canvas.drawPath(mPath, mPaint);
//    }
//
//    /**
//     * dp转px
//     */
//    public int dp2px(Context context, float dpVal) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                dpVal, context.getApplicationContext().getResources().getDisplayMetrics());
//    }
//}
