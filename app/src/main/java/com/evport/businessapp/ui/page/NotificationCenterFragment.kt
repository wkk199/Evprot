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
package com.evport.businessapp.ui.page

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.MessageData
import com.evport.businessapp.data.bean.MessageInfo
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_notification_center.*

/**
 * Create by KunMinX at 19/10/29
 */
class NotificationCenterFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig { //TODO tip: DataBinding 严格模式：
// 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
// 通过这样的方式，来彻底解决 视图调用的一致性问题，
// 如此，视图刷新的安全性将和基于函数式编程的 Jetpack Compose 持平。
// 而 DataBindingConfig 就是在这样的背景下，用于为 base 页面中的 DataBinding 提供绑定项。
// 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910
        return DataBindingConfig(R.layout.fragment_notification_center, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
        //                .addBindingParam(BR.adapter, new DrawerAdapter(getContext()));
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

//        tv_name.text = SPUtils.getInstance().getString(Configs.NAME)
        //        mDrawerViewModel.getLibraryLiveData().observe(getViewLifecycleOwner(), libraryInfos -> {
//            if (mAnimationLoaded && libraryInfos != null) {
//                mDrawerViewModel.list.setValue(libraryInfos);
//            }
//        });
//
//        mDrawerViewModel.requestLibraryInfo();

        LiveBus.getInstance().observeEvent(this, Observer { (type) ->
            if (type == Configs.NOTIFICATION_MSG) {
                getData()

            }
        })
    }

    public override fun loadInitData() {
        super.loadInitData()
        //        if (mDrawerViewModel.getLibraryLiveData().getValue() != null) {
//            mDrawerViewModel.list.setValue(mDrawerViewModel.getLibraryLiveData().getValue());
//        }
        getData()
    }

    inner class ClickProxy {
        fun system() {
            nav().navigate(R.id.action_global_notiSysListFragment)

        }


        fun feedback() {
            nav().navigate(R.id.action_global_notiFeedbackListFragment)

        }

        fun back() {
            nav().navigateUp()
        }

        fun comment() {
            nav().navigate(R.id.action_global_notiCommentListFragment)
        }

        fun relpy() {
            nav().navigate(R.id.action_global_notiReplyListFragment)
        }
    }

    fun getData() {
        object : NetworkBoundResource<MessageInfo>(networkStatusCallback = object :
            NetworkStatusCallback<MessageInfo> {

            override fun onSuccess(data: MessageInfo?) {
                if (data!!.systemMessageUnreadCount == 0) {
                    system_msg_num.visibility = View.GONE
                } else {
                    system_msg_num.visibility = View.VISIBLE
                    system_msg_num.text = getNum(data.systemMessageUnreadCount!!)
                }
                if (data!!.feedbackMessageUnreadCount == 0) {
                    my_feedback_num.visibility = View.GONE
                } else {
                    my_feedback_num.visibility = View.VISIBLE
                    my_feedback_num.text = getNum(data.feedbackMessageUnreadCount!!)
                }
                if (data!!.replyToMineUnreadCount == 0) {
                    reply_to_mine_num.visibility = View.GONE
                } else {
                    reply_to_mine_num.visibility = View.VISIBLE
                    reply_to_mine_num.text = getNum(data.replyToMineUnreadCount!!)
                }
                data.apply {
                    if (!TextUtils.isEmpty(systemMessage)){
                        system_message_hint.text = systemMessage
                    }else{
                        system_message_hint.text =getString(R.string.no_system_message)
                    }
                    if (!TextUtils.isEmpty(feedbackMessage)){
                        feedback_hint.text = feedbackMessage
                    }else{
                        feedback_hint.text = getString(R.string.no_my_feedback)
                    }
                    if (!TextUtils.isEmpty(myCommentMessage)){
                        comment_hint.text = myCommentMessage
                    }else{
                        comment_hint.text = getString(R.string.no_comment)
                    }
                    if (!TextUtils.isEmpty(replyToMineMessage)){
                        reply_hint.text = replyToMineMessage
                    }else{
                        if (replyToMineUnreadCount==0){
                            reply_hint.text = getString(R.string.no_reply_to_mine)
                        }

                    }
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<MessageInfo>> {
                return SingletonFactory.apiService.getMessageInfo(MessageData(systemMessagePk="0"))
            }
        }
    }

    fun getNum(num: Int): String {
        var n="";
        n = if (num>99){
            "99+"
        }else{
            num.toString()
        }
        return n
    }
}