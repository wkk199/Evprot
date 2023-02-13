package com.evport.businessapp.utils.loader.GlideMy;

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.bumptech.glide.request.RequestOptions
import com.evport.businessapp.R


class ImageLoaderOptions private constructor() {

    //图像显示类型
    enum class ScaleType {
        FIT_CENTER, CENTER_CROP, CENTER_INSIDE
    }

    //请求优先级
    enum class Priority {
        IMMEDIATE,
        HIGH,
        NORMAL,
        LOW
    }

    private var options = RequestOptions()

    //设置图像显示类型
    fun ScaleType(type: ScaleType): ImageLoaderOptions {
        when (type) {
            ScaleType.FIT_CENTER -> {
                options.fitCenter()
            }
            ScaleType.CENTER_CROP -> {
                options.centerCrop()
            }
            ScaleType.CENTER_INSIDE -> {
                options.centerInside()
            }
        }
        return this
    }    //设置图像显示类型

    /**
     * 设置圆角图片
     */
    @SuppressLint("CheckResult")
    fun glideRound(dp: Int, cornerType: GlideRoundedCornersTransform.CornerType): ImageLoaderOptions {
        options.transform(GlideRoundedCornersTransform(dp.toFloat(), cornerType))
        return this
    }

    /**
     * 设置请求优先级
     * @param priority
     * *
     * @return
     */
    fun priority(priority: Priority): ImageLoaderOptions {
        when (priority) {
            Priority.LOW -> {
                options.priority(com.bumptech.glide.Priority.LOW)
            }
            Priority.HIGH -> {
                options.priority(com.bumptech.glide.Priority.HIGH)
            }
            Priority.NORMAL -> {
                options.priority(com.bumptech.glide.Priority.NORMAL)
            }
            Priority.IMMEDIATE -> {
                options.priority(com.bumptech.glide.Priority.IMMEDIATE)
            }
        }
        return this
    }


    /**
     * 加载固定大小图片
     * @param size
     * *
     * @return
     */
    @SuppressLint("CheckResult")
    fun override(size: Int): ImageLoaderOptions {
        options.override(size)

        return this
    }

    /**
     * 加载固定大小图片
     * @param size
     * *
     * @return
     */
    @SuppressLint("CheckResult")
    fun override(with: Int,height:Int): ImageLoaderOptions {
        options.override(with,height)
        return this
    }

    /**
     * 设置占位图
     */
    @SuppressLint("CheckResult")
    fun placeholder(@DrawableRes resourceId: Int?): ImageLoaderOptions {
        options.placeholder(resourceId ?: R.color.white_a5)
        return this
    }
    /**
     * 设置占位图
     */
    @SuppressLint("CheckResult")
    fun placeholder(drawable: Drawable): ImageLoaderOptions {
        options.placeholder(drawable)
        return this
    }

    /**
     * 设置占位图
     */
    @SuppressLint("CheckResult")
    fun error(@DrawableRes resourceId: Int?): ImageLoaderOptions {
        options.error(resourceId ?: R.color.white_a5)
        return this
    }

    /**不做渐入渐出的转换
     * @return
     */
    @SuppressLint("CheckResult")
    fun dontTransform(): ImageLoaderOptions {
        options.dontTransform()
        return this
    }

    /**
     * 不做加载动画
     */
    @SuppressLint("CheckResult")
    fun dontAnimate(): ImageLoaderOptions {
        options.dontAnimate()
        return this
    }


    /**设置圆形头像
     * @return
     */
    @SuppressLint("CheckResult")
    fun circleCrop(): ImageLoaderOptions {
        options.circleCrop()
        return this
    }

    @SuppressLint("CheckResult")
    fun build(): RequestOptions {
        return options
    }

    companion object {
        fun builder(): ImageLoaderOptions {
            return ImageLoaderOptions().apply {
                ScaleType(ScaleType.CENTER_CROP)
                    .error(null)
            }
        }
    }
}
