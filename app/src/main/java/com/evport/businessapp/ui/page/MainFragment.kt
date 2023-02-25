package com.evport.businessapp.ui.page

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Connector
import com.evport.businessapp.data.bean.DataBean
import com.evport.businessapp.data.bean.Feedback
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.ChageGunDetailActivity
import com.evport.businessapp.ui.state.MainViewModel
import com.evport.businessapp.ui.view.SelectChargingGunPicker
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.toMD5
import com.gyf.immersionbar.ImmersionBar
import com.kunminx.architecture.ui.callback.EventObserver
import com.kunminx.architecture.utils.SPUtils
import com.lxj.xpopup.XPopup
import com.onesignal.OneSignal
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Create by KunMinX at 19/10/29
 */
class MainFragment : BaseFragment() {
    private var mMainViewModel: MainViewModel? = null
    override fun initViewModel() {
        mMainViewModel = getFragmentViewModel(MainViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_main, mMainViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
        //                .addBindingParam(BR.adapter, new PlaylistAdapter(getContext()));
    }


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.changePwdClick.observe(this,
            EventObserver<Boolean> { aBoolean ->
                if (aBoolean) {
                    nav().navigate(R.id.action_global_mainFragment_to_changePasswordFragment)
                }
            })
        sharedViewModel.chargeSettingClick.observe(
            this,
            EventObserver<Boolean> { aBoolean ->
                if (aBoolean) {
                    nav().navigate(R.id.action_global_mainFragment_to_chargeSettingFragment)
                }
            })
        ll_scan.setOnClickListener {

            RxPermissions(requireActivity())
                .request(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                )
                .subscribe {
                    if (it) {

                        nav().navigate(R.id.action_global_scanFragment)
//                        startActivity(Intent(requireContext(),ScanActivity::class.java))

                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.CAMERA
                            )
                        ) {
                            try {
                                ToastUtils.showShort("we need permission to scan")
                            } catch (e: Exception) {

                            }
                        } else {
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null)
                            )
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }
        }

        sharedViewModel.bindSuccess.observe(
            viewLifecycleOwner,
            EventObserver {
//                nav().navigate(R.id.action_global_createCommentFragment)
//                startActivity<CreateCommentActivity>()
            }
        )
        LiveBus.getInstance().observeEvent(this, Observer { type ->
            when (type.type) {
                "sysMessage" -> {
                    nav().navigate(R.id.action_global_notiSysDetailFragment, Bundle().also {
                        it.putString("pk", type.value.toString())
                    })
                }
                "stopCharging" -> {

                    nav().navigate(R.id.action_global_recordDetailFragment, Bundle().also {
                        it.putString("record", type.value.toString())
                    })
                }
                "comment" -> {

                    nav().navigate(R.id.action_global_notiCommentListFragment)
                }
                "reply" -> {

                    nav().navigate(R.id.action_global_notiReplyListFragment)
                }
                "feedback" -> {
                    val data =
                        Gson().fromJson(type.data, DataBean::class.java)
                    val data1 = Feedback(
                        userName = data.userName,
                        feedbackPk = data.feedbackPk,
                        feedbackDatetime = data.feedbackDatetime,
                        userPk = data.userPk,
                        feedbackContent = data.feedbackContent,
                        feedbackTag = data.feedbackTag,

                        )
                    data1.imgDir = data.imgDir
                    nav().navigate(R.id.action_global_notiFeedbackDetailFragment,
                        Bundle().also { b ->
                            b.putParcelable("feedback", data1)
                        })
                }
                "ChargeGunDialog" -> {
                    var connectors = arrayListOf<Connector>()
                    val listType = object : TypeToken<ArrayList<Connector>>() {}.type
                    connectors = Gson().fromJson(type.value.toString(), listType)
                    XPopup.Builder(requireContext())
                        .asCustom(SelectChargingGunPicker(requireContext(), connectors).apply {
                            setCallBack(
                                okBlock = {
                                    startActivity<ChageGunDetailActivity>(
                                        Pair("pk", it.connectorPk)
                                    )

                                })
                        })
                        .show()
                }
            }

        })
        OneSignal.sendTag("token", SPUtils.getInstance().getString(Configs.TOKEN).toMD5())
//        view_pager
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    1 -> {
                        sharedViewModel.refreshNav2.postValue(true)
                    }

                    0 -> {
                        sharedViewModel.mapRefresh.postValue(true)
                    }
                    3 -> {
                        sharedViewModel.mapRefresh3.postValue(true)
                    }
                    4 -> {
                        sharedViewModel.mapRefreshUser4.postValue(true)
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    1 -> {
                        sharedViewModel.refreshNav2.postValue(false)
                    }

                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {


                when (tab?.position) {
                    1 -> {
                        sharedViewModel.refreshNav2.postValue(true)
                    }
                    0 -> {
                        sharedViewModel.mapRefresh.postValue(true)
                    }
                    3 -> {
                        sharedViewModel.mapRefresh3.postValue(true)
                    }
                    4 -> {
                        sharedViewModel.mapRefreshUser4.postValue(true)
                    }
                }
                index = tab?.position!!
            }
        })


        sharedViewModel.refreshComment.observe(this) {
            if (it) {
                ImmersionBar.with(this).statusBarDarkFont(true).init()
                if (index == 3) {
                    return@observe
                }
                view_pager.currentItem = 3
            }
        }

    }

    companion object {
        var index = -1
    }


    // TODO tip 2：此处通过 DataBinding 来规避 在 setOnClickListener 时存在的 视图调用的一致性问题，
    // 也即，有绑定就有绑定，没绑定也没什么大不了的，总之 不会因一致性问题造成 视图调用的空指针。
    // 如果这么说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350
    inner class ClickProxy {
        fun login() {
            nav().navigate(R.id.action_global_mainFragment_to_loginFragment)
        }
    }
}