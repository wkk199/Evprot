package com.evport.businessapp.utils.loader.GlideMy

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.widget.ImageView

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.evport.businessapp.utils.loader.GlideApp


object ImageLoader {
    private var fadeDuration: Int = 600
    /**
     * 常规方式
     */
    private fun getOptions(): RequestOptions {
        return ImageLoaderOptions.builder()
            .ScaleType(ImageLoaderOptions.ScaleType.CENTER_CROP)
            .build()
    }


    /**
     * 加载Url图片
     */
    fun loadImg(context: Context, url: String, target: ImageView, imageOptions: RequestOptions? = null) {
        val thisOptions = imageOptions ?: getOptions()
        if (isNotFinishing(context)) {
            GlideApp.with(context).load(url).transition(withCrossFade(fadeDuration)).apply(thisOptions).into(target)
        }
    }

    fun loadImg(context: Context, uri: Uri, target: ImageView, imageOptions: RequestOptions? = null) {
        val thisOptions = imageOptions ?: getOptions()
        if (isNotFinishing(context)) {
            GlideApp.with(context).load(uri).transition(withCrossFade(fadeDuration)).apply(thisOptions).into(target)
        }
    }

    /**
     * 加载本地资源
     */
    fun loadImg(context: Context, source: Int, target: ImageView, imageOptions: RequestOptions?) {
        val thisOptions = imageOptions ?: getOptions()
        if (isNotFinishing(context)) {
            GlideApp.with(context)
                .load(source).transition(withCrossFade(fadeDuration))
                .apply(thisOptions).into(target)
        }
    }

    /**
     * 加载Drawable
     */
    fun loadImg(context: Context, drawable: Drawable, target: ImageView, imageOptions: RequestOptions?) {
        val thisOptions = imageOptions ?: getOptions()
        if (isNotFinishing(context)) {
            GlideApp.with(context)
                .load(drawable).transition(withCrossFade(fadeDuration)).apply(thisOptions).into(target)
        }
    }

    /**
     * 加载Bitmap
     */
    fun loadImg(context: Context, bitmap: Bitmap, target: ImageView, imageOptions: RequestOptions?) {
        val thisOptions = imageOptions ?: getOptions()
        if (isNotFinishing(context)) {
            GlideApp.with(context)
                .load(bitmap).transition(withCrossFade(fadeDuration)).apply(thisOptions).into(target)
        }
    }


    fun loadImg(context: Context, image: Image, target: ImageView, imageOptions: RequestOptions?) {
        val thisOptions = imageOptions ?: getOptions()
        if (isNotFinishing(context)) {
            GlideApp.with(context)
                .load(image).transition(withCrossFade(fadeDuration)).apply(thisOptions).into(target)
        }
    }


    private fun isNotFinishing(context: Context): Boolean {
        return if (context is Activity) {
            !context.isFinishing
        } else {
            true
        }
    }
}
