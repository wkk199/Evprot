package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.BillingHistoryViewMode
import com.evport.businessapp.utils.LiveBus

class BillingHistoryFragment : BaseFragment() {
    private var mViewModel: BillingHistoryViewMode? = null
    var currentFragment: Fragment? = null
    var acceptingFragment: AcceptingFragment? = null
    var invoicingSucceededFragment: InvoicingSucceededFragment? = null
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(BillingHistoryViewMode::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_billing_history, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }
    //定义回调
    var callback = object: OnBackPressedCallback(
        true // default to enabled
    ) {
        override fun handleOnBackPressed() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_HISTORY, "", ""))
            nav().navigateUp()
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        acceptingFragment = AcceptingFragment()
        invoicingSucceededFragment = InvoicingSucceededFragment()

        currentFragment = acceptingFragment

        val transaction = activity!!.supportFragmentManager
            .beginTransaction()
        transaction.add(R.id.frameLayout_chargers, currentFragment!!)
            .commit()

        requireActivity().onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback)
    }

    inner class ClickProxy {
        fun back() {
            LiveBus.getInstance().post(EventBean(Configs.INVOICUNG_HISTORY, "", ""))
            nav().navigateUp()
        }

        fun chargers() {
            mViewModel!!.tabPage.set(1)
            switchFragment(acceptingFragment!!)
        }

        fun chargersRecords() {
            mViewModel!!.tabPage.set(2)
            switchFragment(invoicingSucceededFragment!!)
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        val transaction: FragmentTransaction = activity!!.supportFragmentManager
            .beginTransaction()
        if (!targetFragment.isAdded()) {
            transaction
                .hide(currentFragment!!)
                .add(R.id.frameLayout_chargers, targetFragment)
                .commit()
        } else {
            transaction
                .hide(currentFragment!!)
                .show(targetFragment)
                .commit()
        }
        currentFragment = targetFragment
    }
}