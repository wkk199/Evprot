package com.evport.businessapp.utils;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.previewlibrary.enitity.IThumbViewInfo;

public class ImageUrl implements Parcelable,IThumbViewInfo  {
    private String url;  //图片地址
    private Rect mBounds; // 记录坐标

    public ImageUrl(String url) {
        this.url = url;
    }

    protected ImageUrl(Parcel in) {
        url = in.readString();
        mBounds = in.readParcelable(Rect.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeParcelable(mBounds, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageUrl> CREATOR = new Creator<ImageUrl>() {
        @Override
        public ImageUrl createFromParcel(Parcel in) {
            return new ImageUrl(in);
        }

        @Override
        public ImageUrl[] newArray(int size) {
            return new ImageUrl[size];
        }
    };

    @Override
    public String getUrl() {//将你的图片地址字段返回
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public Rect getBounds() {//将你的图片显示坐标字段返回
        return mBounds;
    }

    @Nullable
    @Override
    public String getVideoUrl() {
        return null;
    }

}