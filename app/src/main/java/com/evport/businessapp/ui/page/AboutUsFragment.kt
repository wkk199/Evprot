
package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel


/**
 * Create by KunMinX at 19/10/29
 */
class AboutUsFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_about_us, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

    }

    public override fun loadInitData() {
        super.loadInitData()
        mDrawerViewModel?.version?.set(AppUtils.getAppVersionName())
    }

    inner class ClickProxy {


        fun back() {
            nav().navigateUp()
        }
        fun checkVersion() {
        }


    }

}