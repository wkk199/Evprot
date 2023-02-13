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

package com.evport.businessapp.ui.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ActivityUtils;
import com.githang.statusbar.StatusBarCompat;
import com.kunminx.architecture.domain.manager.NetState;
import com.kunminx.architecture.domain.manager.NetworkStateManager;
import com.kunminx.architecture.utils.AdaptScreenUtils;
import com.kunminx.architecture.utils.ScreenUtils;
import com.evport.businessapp.App;
import com.evport.businessapp.BR;
import com.evport.businessapp.R;
import com.evport.businessapp.ui.callback.SharedViewModel;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

/**
 * Create by KunMinX at 19/8/1
 */
public abstract class BaseActivity extends AppCompatActivity {

    private SharedViewModel mSharedViewModel;
    private ViewModelProvider mActivityProvider;
    private ViewDataBinding mBinding;
    private TextView mTvStrictModeTip;

    protected abstract void initViewModel();

    protected abstract DataBindingConfig getDataBindingConfig();

    /**
     * TODO tip: 警惕使用。非必要情况下，尽可能不在子类中拿到 binding 实例乃至获取 view 实例。使用即埋下隐患。
     * 目前的方案是在 debug 模式下，对获取实例的情况给予提示。
     * <p>
     * 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910
     *
     * @return
     */
    protected ViewDataBinding getBinding() {
        if (isDebug() && mBinding != null) {
            if (mTvStrictModeTip == null) {
                mTvStrictModeTip = new TextView(getApplicationContext());
                mTvStrictModeTip.setAlpha(0.5f);
                mTvStrictModeTip.setTextSize(16);
                mTvStrictModeTip.setBackgroundColor(Color.WHITE);
                mTvStrictModeTip.setText(R.string.debug_activity_databinding_warning);
                ((ViewGroup) mBinding.getRoot()).addView(mTvStrictModeTip);
            }
        }
        return mBinding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 关闭暗黑模式
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
//        BarUtils.setStatusBarLightMode(this, true);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white));

        mSharedViewModel = ((App) getApplicationContext()).getAppViewModelProvider(this).get(SharedViewModel.class);

        // loading = new LoadingView(this);
        dialog = new ZLoadingDialog(this);
        dialog.setLoadingBuilder(Z_TYPE.ROTATE_CIRCLE)
                .setLoadingColor(Color.parseColor("#ffffff"))
                .setHintText("正在加载中...")
                .setHintTextSize(16) // 设置字体大小
                .setHintTextColor(Color.WHITE)  // 设置字体颜色
//                                .setDurationTime(0.5) // 设置动画时间百分比
                .setDialogBackgroundColor(Color.parseColor("#cc111111"));// 设置背景色

        getLifecycle().addObserver(NetworkStateManager.getInstance());

        initViewModel();
        DataBindingConfig dataBindingConfig = getDataBindingConfig();

        //TODO tip: DataBinding 严格模式：
        // 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
        // 通过这样的方式，来彻底解决 视图调用的一致性问题，
        // 如此，视图刷新的安全性将和基于函数式编程的 Jetpack Compose 持平。

        // 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910

        ViewDataBinding binding = DataBindingUtil.setContentView(this, dataBindingConfig.getLayout());
        binding.setLifecycleOwner(this);
        binding.setVariable(BR.vm, dataBindingConfig.getStateViewModel());
        SparseArray bindingParams = dataBindingConfig.getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }
        mBinding = binding;
        NetworkStateManager.getInstance().networkStateCallback.observe(this, this::onNetworkStateChanged);
        init();
        Log.e("top", ActivityUtils.getTopActivity().getLocalClassName());
    }

    public boolean isDebug() {
        return getApplicationContext().getApplicationInfo() != null &&
                (getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    @Override
    public Resources getResources() {
        if (ScreenUtils.isPortrait()) {
            return AdaptScreenUtils.adaptWidth(super.getResources(), 360);
        } else {
            return AdaptScreenUtils.adaptHeight(super.getResources(), 640);
        }
    }

    protected void showLongToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    protected void init() {

    }

    protected void showShortToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(int stringRes) {
        showLongToast(getApplicationContext().getString(stringRes));
    }

    protected void showShortToast(int stringRes) {
        showShortToast(getApplicationContext().getString(stringRes));
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(this);
        }
        return mActivityProvider.get(modelClass);
    }

    public SharedViewModel getSharedViewModel() {
        return mSharedViewModel;
    }

    private boolean isDestroy = false;
    //private LoadingView loading;
    ZLoadingDialog dialog;

    public void startLoading() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void stopLoading() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    Handler h;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dismissLoading();
//
//           new  AlertDialog
//                   .Builder(getBaseContext())
//                   .setTitle("Operation timed out")
//                   .setMessage("The operation may be successful, please click confirm to return to the previous page")
//                   .setPositiveButton("confirm",new DialogInterface.OnClickListener(){
//
//                       @Override
//                       public void onClick(DialogInterface dialog, int which) {
//                           dialog.dismiss();
//
//                       }
//                   })
//                   .create().show();
        }
    };
    private BasePopupView mLoadingDialog = null;

    public void showLoading() {


        runOnUiThread(
                () -> {
//                            h = new Handler();
//                            h.postDelayed(runnable, 10 * 1000);
//                        ((BaseActivity) getActivity()).showLoading();
                    if (mLoadingDialog == null)
                        mLoadingDialog = new XPopup.Builder(this).asLoading("", R.layout.dialog_loading);
                    mLoadingDialog.show();
                }
        );


    }

//    public void showLoading() {
//      /*  startLoading();
//        if (!isDestroy && !loading.isShowing()) {
//            h = new Handler();
//            h.postDelayed(runnable, 10 * 1000);
//            loading.show();
//        }*/
//        dialog.show();
//    }

    public void showLoading(int delayTime) {
     /*   startLoading();
        if (!isDestroy && !loading.isShowing()) {
            h = new Handler();
            h.postDelayed(runnable, delayTime * 1000);
            loading.show();
        }*/
        dialog.show();

    }

    public void dismissLoading() {
        runOnUiThread(
                () -> {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.smartDismiss();
                        mLoadingDialog = null;
                    }
                }
        );

    }

//    public void dismissLoading() {
//    /*    stopLoading();
//        if (!isDestroy && loading.isShowing())
//            loading.dismiss();*/
//        dialog.dismiss();
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        if (h != null && runnable != null)
            h.removeCallbacks(runnable);

    }


    @SuppressWarnings("EmptyMethod")
    protected void onNetworkStateChanged(NetState netState) {
        //TODO 子类可以重写该方法，统一的网络状态通知和处理
    }

    /**
     * 隐藏键盘
     */
    public void closeKeyWord() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     */
    public void showKeyWord() {
        /** 弹出软键盘 **/
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
