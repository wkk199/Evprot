package com.evport.businessapp.ui.view
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import com.evport.businessapp.R
import com.kunminx.architecture.utils.AdaptScreenUtils

/**
 * Created by jingzz on 2020/3/20.
 */
class RadiusCardView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    CardView(context, attrs, defStyleAttr) {
    private var tlRadiu: Float
    private var trRadiu: Float
    private var brRadiu: Float
    private var blRadiu: Float

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.materialCardViewStyle
    ) {
    }

    init {
        radius = 0f
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RadiusCardView)
        tlRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_topLeftRadiu, 0f)
        trRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_topRightRadiu, 0f)
        brRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_bottomRightRadiu, 0f)
        blRadiu = array.getDimension(R.styleable.RadiusCardView_rcv_bottomLeftRadiu, 0f)
        background = ColorDrawable()
    }

    override fun onDraw(canvas: Canvas) {
        val path = Path()
        val rectF = rectF
        val readius =
            floatArrayOf(tlRadiu, tlRadiu, trRadiu, trRadiu, brRadiu, brRadiu, blRadiu, blRadiu)
        path.addRoundRect(rectF, readius, Path.Direction.CW)
        canvas.clipPath(path, Region.Op.INTERSECT)
        super.onDraw(canvas)
    }



    fun setTopRadiu(tlRadiu :Float,trRadiu :Float){
        this.tlRadiu = AdaptScreenUtils.dp2px(context,tlRadiu).toFloat()
        this.trRadiu =AdaptScreenUtils.dp2px(context,trRadiu).toFloat()

        invalidate()

    }
    fun setBottonRadiu(tlRadiu :Float,trRadiu :Float){
        this.brRadiu = AdaptScreenUtils.dp2px(context,tlRadiu).toFloat()
        this.blRadiu =AdaptScreenUtils.dp2px(context,trRadiu).toFloat()

        invalidate()

    }

    private val rectF: RectF
        private get() {
            val rect = Rect()
            getDrawingRect(rect)
            return RectF(rect)
        }
}