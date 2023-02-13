/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evport.businessapp.ui.base.binding_adapter;

import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.kunminx.architecture.utils.ClickUtils;
import com.kunminx.architecture.utils.NetworkUtils;
import com.evport.businessapp.R;

/**
 * Create by KunMinX at 19/9/18
 */
@SuppressWarnings("unused")
public class CommonBindingAdapter {

    @BindingAdapter(value = {"imageUrl", "placeHolder", "circleCrop"}, requireAll = false)
    public static void loadUrl(ImageView view, String url, Drawable placeHolder, Boolean circleCrop) {
        String mUrl = url;
        if (SPUtils.getInstance().getBoolean("IsWifiLoadb")&&!NetworkUtils.isWifiAvailable()){
            mUrl ="";
        }
        if (circleCrop != null && circleCrop)
            Glide.with(view.getContext()).load(mUrl).error(R.color.gray).placeholder(placeHolder).circleCrop().into(view);
        else
            Glide.with(view.getContext()).load(mUrl).placeholder(placeHolder).centerCrop().into(view);

    }

    @BindingAdapter(value = {"visible"}, requireAll = false)
    public static void visible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter(value = {"invisible"}, requireAll = false)
    public static void invisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter(value = {"passwordInputType"}, requireAll = false)
    public static void passwordInputType(EditText view, boolean visible) {
//        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible)
            view.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
        else
            view.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
    }

    @BindingAdapter(value = {"showDrawable", "drawableShowed"}, requireAll = false)
    public static void showDrawable(ImageView view, boolean showDrawable, int drawableShowed) {
        view.setImageResource(showDrawable ? drawableShowed : android.R.color.transparent);
    }

    @BindingAdapter(value = {"textColor"}, requireAll = false)
    public static void setTextColor(TextView textView, int textColorRes) {
        textView.setTextColor(textView.getResources().getColor(textColorRes));
    }

    @BindingAdapter(value = {"imageRes"}, requireAll = false)
    public static void setImageRes(ImageView imageView, int imageRes) {
        imageView.setImageResource(imageRes);
    }

    @BindingAdapter(value = {"selected"}, requireAll = false)
    public static void selected(View view, boolean select) {
        view.setSelected(select);
    }


    @BindingAdapter(value = {"onClickWithDebouncing"}, requireAll = false)
    public static void onClickWithDebouncing(View view, View.OnClickListener clickListener) {
        ClickUtils.applySingleDebouncing(view, clickListener);
    }

    @BindingAdapter(value = {"adjustWidth"})
    public static void adjustWidth(View view, int adjustWidth) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = adjustWidth;
        view.setLayoutParams(params);
    }

    @BindingAdapter(value = {"adjustHeight"})
    public static void adjustHeight(View view, int adjustHeight) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = adjustHeight;
        view.setLayoutParams(params);
    }
}
