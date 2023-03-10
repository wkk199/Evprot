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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.kunminx.architecture.domain.manager.NetState;
import com.kunminx.architecture.domain.manager.NetworkStateManager;
import com.evport.businessapp.App;
import com.evport.businessapp.BR;
import com.evport.businessapp.R;
import com.evport.businessapp.ui.callback.SharedViewModel;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;


/**
 * Create by KunMinX at 19/7/11
 */
public abstract class BaseFragment extends Fragment implements LifecycleObserver {

    private static final Handler HANDLER = new Handler();
    protected AppCompatActivity mActivity;
    private SharedViewModel mSharedViewModel;
    protected boolean mAnimationLoaded;
    private ViewModelProvider mFragmentProvider;
    private ViewModelProvider mActivityProvider;
    private ViewDataBinding mBinding;
    private TextView mTvStrictModeTip;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    protected abstract void initViewModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedViewModel = ((App) mActivity.getApplicationContext()).getAppViewModelProvider(mActivity).get(SharedViewModel.class);

        initViewModel();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO ?????? liveData ??? lambda ?????????????????????????????????????????? Cannot add the same observer with different lifecycles ????????????
        // ?????????https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        NetworkStateManager.getInstance().networkStateCallback.observe(getViewLifecycleOwner(), this::onNetworkStateChanged);
    }

    @SuppressWarnings("EmptyMethod")
    protected void onNetworkStateChanged(NetState netState) {
        //TODO ??????????????????????????????????????????????????????????????????
    }

    protected abstract DataBindingConfig getDataBindingConfig();

    /**
     * TODO tip: ?????????????????????????????????????????????????????????????????? binding ?????????????????? view ?????????????????????????????????
     * ????????????????????? debug ???????????????????????????????????????????????????
     * <p>
     * ?????????????????????????????????????????? https://xiaozhuanlan.com/topic/9816742350 ??? https://xiaozhuanlan.com/topic/2356748910
     *
     * @return
     */
    protected ViewDataBinding getBinding() {
        if (isDebug() && mBinding != null) {
            if (mTvStrictModeTip == null) {
                mTvStrictModeTip = new TextView(getContext());
                mTvStrictModeTip.setAlpha(0.5f);
                mTvStrictModeTip.setTextSize(16);
                mTvStrictModeTip.setBackgroundColor(Color.WHITE);
                mTvStrictModeTip.setText(R.string.debug_fragment_databinding_warning);
                ((ViewGroup) mBinding.getRoot()).addView(mTvStrictModeTip);
            }
        }
        return mBinding;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        DataBindingConfig dataBindingConfig = getDataBindingConfig();

        //TODO tip: DataBinding ???????????????
        // ??? DataBinding ??????????????? base ???????????????????????????????????????
        // ??????????????????????????????????????? ?????????????????????????????????
        // ??????????????????????????????????????????????????????????????? Jetpack Compose ?????????

        // ?????????????????????????????????????????? https://xiaozhuanlan.com/topic/9816742350 ??? https://xiaozhuanlan.com/topic/2356748910

        ViewDataBinding binding = DataBindingUtil.inflate(inflater, dataBindingConfig.getLayout(), container, false);
        binding.setLifecycleOwner(this);
        binding.setVariable(BR.vm, dataBindingConfig.getStateViewModel());
        SparseArray bindingParams = dataBindingConfig.getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }
        mBinding = binding;
        return binding.getRoot();
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //TODO ????????????????????? UI ?????????????????????????????????????????????
        HANDLER.postDelayed(() -> {
            if (!mAnimationLoaded) {
                mAnimationLoaded = true;
                loadInitData();
            }
        }, 280);
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    protected void loadInitData() {

    }

    public boolean isDebug() {
        return mActivity.getApplicationContext().getApplicationInfo() != null &&
                (mActivity.getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    protected void showLongToast(String text) {
        Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String text) {
        Toast.makeText(mActivity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(int stringRes) {
        showLongToast(mActivity.getApplicationContext().getString(stringRes));
    }

    protected void showShortToast(int stringRes) {
        showShortToast(mActivity.getApplicationContext().getString(stringRes));
    }

    protected <T extends ViewModel> T getFragmentViewModel(@NonNull Class<T> modelClass) {
        if (mFragmentProvider == null) {
            mFragmentProvider = new ViewModelProvider(this);
        }
        return mFragmentProvider.get(modelClass);
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = new ViewModelProvider(mActivity);
        }
        return mActivityProvider.get(modelClass);
    }

    protected NavController nav() {
        return NavHostFragment.findNavController(this);
    }

    public SharedViewModel getSharedViewModel() {
        return mSharedViewModel;
    }


    //    Handler h;
//
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            dismissLoading();
//        }
//    };
    private BasePopupView mLoadingDialog = null;

    public void showLoading() {

        if (getActivity() != null) {
            getActivity().runOnUiThread(
                    () -> {
//                            h = new Handler();
//                            h.postDelayed(runnable, 10 * 1000);
//                        ((BaseActivity) getActivity()).showLoading();
                        if (mLoadingDialog == null)
                            mLoadingDialog = new XPopup.Builder(getActivity()).asLoading("", R.layout.dialog_loading);
                        mLoadingDialog.show();
                    }
            );

        }


    }

//    public void showLoading() {
//
//        if (getActivity() != null) {
//            getActivity().runOnUiThread(
//                    () -> {
////                            h = new Handler();
////                            h.postDelayed(runnable, 10 * 1000);
//                        ((BaseActivity) getActivity()).showLoading();
//
//                    }
//            );
//
//        }
//    }

    public void showLoading(int delayTime) {

//        if (getActivity() != null) {
//            getActivity().runOnUiThread(
//                    () -> {
////                            h = new Handler();
////                            h.postDelayed(runnable, 10 * 1000);
//                        ((BaseActivity) getActivity()).showLoading(delayTime);
//
//                    }
//            );
//
//        }
    }

    public void dismissLoading() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(
                    () -> {
                        if (mLoadingDialog != null) {
                            mLoadingDialog.smartDismiss();
                            mLoadingDialog = null;
                        }
                    }
            );
        }
    }

//    @Override
//    public void onDestroyView() {
//
//        if (h != null && runnable != null)
//            h.removeCallbacks(runnable);
//        super.onDestroyView();
//    }

    /**
     * ????????????
     */
    public void closeKeyWord() {
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * ????????????
     */
    public void showKeyWord() {
        /** ??????????????? **/
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
