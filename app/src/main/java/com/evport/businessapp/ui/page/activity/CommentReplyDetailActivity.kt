package com.evport.businessapp.ui.page.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.CommentReplyAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi
import com.evport.businessapp.utils.toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_comment_detail.*
import kotlinx.android.synthetic.main.activity_reply_detail.et_reply
import kotlinx.android.synthetic.main.activity_reply_detail.iv_delete
import kotlinx.android.synthetic.main.activity_reply_detail.iv_image
import kotlinx.android.synthetic.main.activity_reply_detail.ratingBar
import kotlinx.android.synthetic.main.activity_reply_detail.rl_top
import kotlinx.android.synthetic.main.activity_reply_detail.tv_date
import kotlinx.android.synthetic.main.activity_reply_detail.tv_has_del
import kotlinx.android.synthetic.main.activity_reply_detail.tv_name
import kotlinx.android.synthetic.main.activity_reply_detail.tv_reply
import kotlinx.android.synthetic.main.activity_reply_detail.tv_score
import kotlinx.android.synthetic.main.activity_reply_detail.tv_street


class CommentReplyDetailActivity : BaseActivity() {
    private lateinit var mStationViewModel: StationViewModel
    private val commentPk by lazy {
        intent.getParcelableExtra("comment") as Comment?
    }
    private val commentsReplyPk by lazy {
        intent.getStringExtra("commentsReplyPk")
    }
    var replyListItem: ReplyListItem? = null
    var addNewPK = ""
    var addType = "reply"
    var hint = ""
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_reply_detail, mStationViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
//            .addBindingParam(
//                BR.info,
//                comment
//            )
            .addBindingParam(
                BR.adapter,
                CommentReplyAdapter(baseContext).apply {
                    setOnItemClickListener { item, position ->
                        addNewPK = item?.commentsReplyPk.toString()
                        addType = "reply"
                        hint = "${resources.getString(R.string.reply)}${item?.replyName.toString()}"+"..."
                        et_reply.setText("")
                        et_reply.setHint(hint)
                        showSoftInputFromWindow()

                    }
                    delClick = {
                        val b = it.hasDelFlag()
                        it.delFlag = b.toString()
                        notifyDataSetChanged()
                        delReply(it.commentsReplyPk.toString(),false)
                    }
                }
            )
    }

    override fun initViewModel() {
        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (comment.hasMoreReplyComment) {

        mReplyList.clear()
//            comment.replyComment?.let {
////                mReplyList.addAll(it)
//
//            }

        if (!commentPk?.userName.isNullOrBlank()) {
            hint = "${resources.getString(R.string.reply)}  ${commentPk?.userName}"+"..."
        } else {
            hint = "${resources.getString(R.string.reply)} "+"..."
        }
        et_reply.setText("")
        et_reply.setHint(hint)

//        mStationViewModel.listReplyDetail.value = mReplyList
        getData()
        feedbackReplyCheck();
//        } else {
//            iv_image.setImageURI(comment.avatarUrl)
//            getData()
//
//            tv_reply.text = "${comment.replyCounts} replies"
//        }
//        et_reply.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
//
//            false
//        })
        addNewPK = commentPk?.commentsReplyRootPk ?: commentPk?.commentsReplyPk?:commentPk?.commentsPk?:""
        initStatus()

        mReplyList.clear()
        mStationViewModel.listReplyDetail.value = mReplyList
        et_reply.doOnTextChanged { text, start, before, count ->
            if (text!!.length==500){
                ToastUtils.showShort("Comments should not exceed 500 words")
            }
        }
        list_reply.isNestedScrollingEnabled = false;
    }

    fun initStatus() {
        replyListItem?.apply {
            iv_delete.setOnClickListener {
                 delFlag = (! hasDelFlag()).toString()
                initStatus()
                delReply( this.commentsReplyPk.toString(),true)
            }
            rl_top.setOnClickListener {
                addType = "reply"
                hint = "reply ${this.replyName}"+"..."
                et_reply.setText("")
                et_reply.setHint(hint)
                addNewPK = commentPk?.commentsReplyRootPk ?: commentPk?.commentsReplyPk?:commentPk?.commentsPk?:""
                showSoftInputFromWindow()
            }
            ratingBar.visibility = View.GONE

            iv_image.setImageIsWifi( replyAvatarUrl)
            tv_name.text = replyName
            tv_street.text =  replyContent
            tv_date.text =  commentDateString()
            if (! hasDelFlag()) {
                if (baseContext.getUser()?.userPk ==  replyUserPk) {
                    iv_delete.visibility = View.VISIBLE
                } else {
                    iv_delete.visibility = View.GONE
                }

                tv_name.visibility = View.VISIBLE
                tv_score.visibility = View.VISIBLE
                tv_street.visibility = View.VISIBLE
                tv_has_del.visibility = View.GONE
            } else {
                iv_delete.visibility = View.GONE
                tv_name.visibility = View.INVISIBLE
                tv_score.visibility = View.GONE
                tv_street.visibility = View.INVISIBLE
                tv_has_del.visibility = View.VISIBLE
            }
        }

    }


    inner class ClickProxy {
        fun back() {
            finish()
        }

        fun send() {

            if (addNewPK.isNotEmpty() && et_reply.text.toString().trim().isNotEmpty()){
                showLoading()
                addRep()
            }
            else
                showLongToast("Please enter your comments")
        }

    }

    var mReplyList = ArrayList<ReplyDetail>()
    fun getData() {
        showLoading()
        val pk = commentPk?.commentsReplyRootPk ?: commentPk?.commentsReplyPk
        object : NetworkBoundResource<ReplyListItem>(networkStatusCallback = object :
            NetworkStatusCallback<ReplyListItem> {

            override fun onSuccess(data: ReplyListItem?) {
                dismissLoading()
//                if (data != null) {
                data?.detail?.apply {
                    Log.e("hm--reply",Gson().toJson(data))
                    tv_reply.text = "${this.size} ${resources.getString(R.string.replies)}"
                    mReplyList.clear()
                    mReplyList.addAll(this)
                    mStationViewModel.listReplyDetail.value = mReplyList
                }
                replyListItem = data
                initStatus()

//                }
                closeKeyWord()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ReplyListItem>> {
                return SingletonFactory.apiService.getReplyDetail(Comment(commentsReplyRootPk = pk))

            }
        }
    }


    fun delReply(pk: String,type:Boolean) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                dismissLoading()
                if (type){
                    LiveBus.getInstance().post(EventBean(Configs.NOTIFICATION_MSG, true, ""))
                    this@CommentReplyDetailActivity.finish()
                }else{
                    getData()
                }


            }

            override fun onFailure(message: String) {
                dismissLoading()

                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delReply(Comment(commentsReplyPk = pk, check = null))
            }
        }
    }

    fun addRep() {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                dismissLoading()
                getData()
                et_reply.clearFocus()
                et_reply.setText("")

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.addRep(
                    CommitComment(
                        type = addType,
                        sourcePk = addNewPK,
                        content = et_reply.text.toString()
                    )
                )
            }
        }
    }

    fun feedbackReplyCheck() {
        //  val pk = commentPk?.commentsReplyRootPk ?: commentPk?.commentsReplyPk
        val pk = commentsReplyPk
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                LiveBus.getInstance().post(EventBean(Configs.NOTIFICATION_MSG, true, ""))
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.commentsReplyCheck(
                    MessageData(commentsReplyPk = pk)
                )
            }
        }
    }

    fun showSoftInputFromWindow() {
        et_reply.isFocusable = true
        et_reply.isFocusableInTouchMode = true
        et_reply.requestFocus()
        //显示软键盘
        showKeyWord()
    }
}