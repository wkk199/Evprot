package com.evport.businessapp.utils;

/**
 * Author:houming
 * Date: 2019/6/3
 * Organization:英杰讯和科技有限公司
 */
public class NoFastClickUtils {
    private static long lastClickTime = 0;//上次点击的时间
    private static int spaceTime = 500;

    // 时间间隔
    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        //当前系统时间
        boolean isAllowClick;
        //是否允许点击
        if (currentTime - lastClickTime < spaceTime) {
            isAllowClick = false;
        } else {
            isAllowClick = true;
        }
        lastClickTime = currentTime;
        return isAllowClick;
    }


}
