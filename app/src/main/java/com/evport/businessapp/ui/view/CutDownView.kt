package com.evport.businessapp.ui.view

import android.content.Context
import android.graphics.Canvas
import android.os.HandlerThread
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.badoo.mobile.util.WeakHandler
import com.evport.businessapp.utils.DateUtil.formatTimeStamp
import com.evport.businessapp.utils.toDayFrendly2
import java.util.*


class CutDownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var handlerThread: HandlerThread? = null
    private var weakHandler: WeakHandler? = null
    var time: String? = null
    var up = false

    //    倒计时
    fun setDownTime(time: String) {
        this.time = time
        up = false
//        handlerThread = HandlerThread(TAG)
//        handlerThread!!.start()
//        val looper = handlerThread!!.looper
//        weakHandler = WeakHandler(looper, Handler.Callback { msg: Message ->
//            if (msg.what == MESSAGE_UPDATE) {
//                cutDown()
//            }
//            true
//        })
        if (!this.time.isNullOrEmpty()){
            cutDown()
        }
    }

    var onDownFinish:(()->Any?) ?=null
    //    正计时
    fun setUpTime(time: String) {
        this.time = time
        up = true
        if (!this.time.isNullOrEmpty()){
            cutDown()
        }
        this.endTime = ""
    }
    var endTime = ""
    fun setUpTime(time: String,endTime:String) {
        this.time = time
        up = true
        if (!this.time.isNullOrEmpty()){
            cutDown()
        }
        this.endTime = endTime
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }


    private fun cutDown() {
//        this.isFocusableInTouchMode = false
//        this.isFocusable = false
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            this.focusable = View.NOT_FOCUSABLE
//        }

        val remainingTime =
            if (up)
                Date().time - formatTimeStamp(time.toString()) + 1000
            else
                formatTimeStamp(time.toString()) - Date().time + 1000

        try {
            if (remainingTime <= 0){
                onDownFinish?.invoke()
            }else  {

                val hour: Int = (remainingTime / 1000 / 3600).toInt()
                val minute: Int = (((remainingTime) - 1000 * hour * 3600) / 1000 / 60).toInt()
                val second: Int =
                    ((remainingTime - 1000 * hour * 3600 - minute * 60 * 1000) / 1000).toInt()

                if (hour == 0&&minute == 0&&second==0){
                    onDownFinish?.invoke()
                }
                this.let {
                    val format = "%1$02d"
                    val t = endTime.toDayFrendly2()
                    if (endTime.isNotBlank())
                        it.text = "${String.format(format,hour)}:${String.format(format,minute)}:${String.format(format,second)}/$t"
                    else
                        it.text = "${String.format(format,hour)}:${String.format(format,minute)}:${String.format(format,second)}"

                    it.postDelayed({
                        cutDown()
                    },1000)
//                    weakHandler!!.sendEmptyMessageDelayed(MESSAGE_UPDATE, 1000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
//        weakHandler!!.removeCallbacksAndMessages(null)
//        handlerThread!!.quit()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    companion object {
        private val TAG = CutDownView::class.java.simpleName
        private const val MESSAGE_UPDATE = 1


    }

//    init {
//        val ta = context.obtainStyledAttributes(attrs, R.styleable.BubbleView)
//        time = ta.getString(R.styleable.CutDownView_time)
//    }
}