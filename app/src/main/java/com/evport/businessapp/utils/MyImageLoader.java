package com.evport.businessapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.evport.businessapp.R;
import com.previewlibrary.loader.IZoomMediaLoader;
import com.previewlibrary.loader.MySimpleTarget;

/**
 * Create by ljx at 2020/12/4
 **/
public class MyImageLoader implements IZoomMediaLoader {
    RequestOptions options;

    {
        options = new RequestOptions()
                .centerCrop()
//                .placeholder(R.drawable.ic_default_image)
//                .error(R.drawable.ic_default_image)
                .priority(Priority.HIGH);
    }

//    @Override
//    public void displayImage(Fragment context, String path, final MySimpleTarget<Bitmap> simpleTarget) {
//
//
//    }

    @Override
    public void displayImage(@NonNull Fragment context, @NonNull String path, ImageView imageView, @NonNull MySimpleTarget simpleTarget) {

        Glide
                .with(context)
                .asBitmap()
                .load(path)
                .error(R.color.white)
                .listener(new RequestListener<Bitmap>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        simpleTarget.onLoadFailed(context.getResources().getDrawable(R.drawable.above_shadow));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        simpleTarget.onResourceReady();
                        return false;
                    }

//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<String> target, boolean isFirstResource) {
//
//                        simpleTarget.onLoadFailed(null);
//                        return false;
//
//                    }
//
//                    @Override
//                    public boolean onResourceReady(String resource, Object model, Target<String> target, DataSource dataSource, boolean isFirstResource) {
//                        simpleTarget.onResourceReady();
//                        return false;
//                    }

                })
                .into(imageView);
//        Glide.with(fragment)
//                .asBitmap()
//                .load(s)
//                .apply(options)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                        mySimpleTarget.onResourceReady();
//                    }
//                    @Override
//                    public void onLoadStarted(Drawable placeholder) {
//                        super.onLoadStarted(placeholder);
//
//                    }
//                    @Override
//                    public void onLoadFailed(Drawable errorDrawable) {
//                        super.onLoadFailed(errorDrawable);
//                        mySimpleTarget.onLoadFailed(errorDrawable);
//                    }
//                });
    }

    @Override
    public void displayGifImage(@NonNull Fragment fragment, @NonNull String s, ImageView imageView, @NonNull MySimpleTarget mySimpleTarget) {

    }

    @Override
    public void onStop(@NonNull Fragment context) {
        Glide.with(context).onStop();
    }
    @Override
    public void clearMemory(@NonNull Context c) {
        Glide.get(c).clearMemory();
    }
}
