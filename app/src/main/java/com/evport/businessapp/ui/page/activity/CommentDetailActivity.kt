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
import com.evport.businessapp.ui.page.adapter.StationCommentReplyAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.LiveBus
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.setImageIsWifi
import com.evport.businessapp.utils.toast
import com.gyf.immersionbar.ImmersionBar
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_comment_detail.*
import org.jetbrains.anko.startActivity


class CommentDetailActivity : BaseActivity() {
    private lateinit var mStationViewModel: StationViewModel
    private val comment by lazy {
        intent.getParcelableExtra("comment") as Comment?
    }
    private val commentsReplyPk by lazy {
        intent.getStringExtra("commentsReplyPk")
    }
    private val appCommentsPk by lazy {
        intent.getStringExtra("appCommentsPk")
    }
    private val type by lazy {
        intent.getIntExtra("type", 0)
    }
    var addNewPK = ""
    var addType = "comment"
    var hint = ""
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_comment_detail, mStationViewModel)
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
                StationCommentReplyAdapter(baseContext).apply {
                    setOnItemClickListener { item, position ->
                        addNewPK = item?.commentsReplyPk.toString()
                        addType = "reply"
                        hint = "${resources.getString(R.string.reply)} ${item?.replyName.toString()}"
                        et_reply.setText("")
                        et_reply.setHint(hint)
                        showSoftInputFromWindow()
                    }
                    viewAllClick = {
                        startActivity<CommentReplyDetailActivity>(
                            Pair("comment", it),
                            Pair("commentsReplyPk", it!!.commentsReplyPk)
                        )
                    }
                    delClick = {
                        val b = it.hasDelFlag()
                        it.delFlag = b.toString()
                        notifyDataSetChanged()
                        delReply(it.commentsReplyPk.toString())
                    }
                    delClickRep = {
                        delReply(it.commentsReplyPk.toString())
                    }
                    itemClick = { name, pk, type ->
                        addNewPK = pk
                        addType = type
                        hint = "${resources.getString(R.string.reply)} $name"
                        et_reply.setText("")
                        et_reply.setHint(hint)
                        Log.e("hm----","12345")
                        showSoftInputFromWindow()

                    }
                }
            )
    }


    override fun initViewModel() {
        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStationViewModel.Comment.value = comment
        Log.e("hm---comment", Gson().toJson(comment))

        addNewPK = if (comment?.commentsPk.isNullOrBlank())
            comment?.appCommentsPk.toString()
        else
            comment?.commentsPk.toString()

//            comment?.commentsPk.toString()
//        if (comment.hasMoreReplyComment) {
//
//            mReplyList.clear()
//            comment.replyComment?.let {
////                mReplyList.addAll(it)
//                tv_reply.text = "${it.size} replies"
//            }
//
//            mStationViewModel.listCommentReply.value = mReplyList
//            iv_image.setImageURI(comment.replyAvatarUrl)
////            tv_name.text = comment.replyName
////            tv_street.text = comment.replyContent
//            tv_date.text = comment.commentDateString()
//            tv_comments.visibility = View.GONE
//            getData()
//        } else {
        iv_image.setImageIsWifi(comment?.avatarUrl)
        getData()
        if (type != 2) {
            feedbackReplyCheck()
        }

        tv_reply.text = "${comment?.replyCounts}  ${resources.getString(R.string.replies)}"
//        }
        mReplyList.clear()
        mStationViewModel.listCommentReply.value?.clear()
        comment?.ratingString()?.apply {

            ratingBar.rating = if (this.isNotEmpty()) comment?.ratingString()!!.toFloat() else 0.0f
        }
//        et_reply.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
//
//            false
//        })
        initStatus()
        iv_delete.setOnClickListener {
            comment?.delFlag = (!comment?.hasDelFlag()!!).toString()
            initStatus()
            delComment(addNewPK)
        }
        rl_top.setOnClickListener {
            addType = "comment"
            if (!comment?.userName.isNullOrBlank()) {
                hint = "${resources.getString(R.string.reply)}  ${comment?.userName}"
            } else {
                hint = "${resources.getString(R.string.reply)} "
            }
            et_reply.setText("")
            et_reply.setHint(hint)

            addNewPK = if (comment?.commentsPk.isNullOrBlank())
                comment?.appCommentsPk.toString()
            else
                comment?.commentsPk.toString()
            showSoftInputFromWindow()
        }
        et_reply.doOnTextChanged { text, start, before, count ->
            if (text!!.length == 500) {
                ToastUtils.showShort("Comments should not exceed 500 words")
            }
        }
        list_comment.isNestedScrollingEnabled = false;
        list_reply.isNestedScrollingEnabled = false;
    }

    fun initStatus() {
        if (!comment?.hasDelFlag()!!) {
            if (baseContext.getUser()?.userPk == comment?.appUserPk) {
                iv_delete.visibility = View.VISIBLE
            } else {
                iv_delete.visibility = View.GONE
            }

            tv_comments.visibility = View.VISIBLE
            tv_name.visibility = View.VISIBLE
            tv_score.visibility = View.VISIBLE
            tv_street.visibility = View.VISIBLE
            tv_has_del.visibility = View.GONE
        } else {
            tv_comments.visibility = View.GONE
            iv_delete.visibility = View.GONE
            tv_name.visibility = View.VISIBLE
            tv_score.visibility = View.GONE
            tv_street.visibility = View.INVISIBLE
            tv_has_del.visibility = View.VISIBLE
        }

//        tv_reply.text = "${c?.replyCounts} replies"
//
//        mStationViewModel.Comment.value = comment
//
//        addNewPK = comment?.commentsPk.toString()
//        iv_image.setImageURI(comment?.avatarUrl)
//        getData()
//
//        tv_reply.text = "${comment?.replyCounts} replies"
//        mReplyList.clear()
//        mStationViewModel.listCommentReply.value?.clear()
//        c.rating?.apply {
//
//            ratingBar.rating = if (this.isNotEmpty()) this.toFloat() else 0.0f
//        }

    }

    inner class ClickProxy {
        fun back() {
            finish()
        }


        fun send() {
            if (addNewPK.isNotEmpty() && et_reply.text.toString().trim().isNotEmpty()) {
                showLoading()
                addRep()
            } else
                showLongToast("请输入评论内容")
        }

    }

    var mReplyList = ArrayList<Comment>()

    fun getData() {
        val request = if (comment?.appCommentsPk.isNullOrBlank())
            Comment(commentsReplyPk = comment?.commentsReplyPk)
        else
            Comment(commentPk = comment?.appCommentsPk)
        object : NetworkBoundResource<List<Comment>>(networkStatusCallback = object :
            NetworkStatusCallback<List<Comment>> {

            override fun onSuccess(data: List<Comment>?) {
//                if (data != null) {
                data?.apply {

                    val fatherList = filter { it.commentsReplyRootPk == null }
                    val childList = filter { it.commentsReplyRootPk != null }
                    tv_reply.text = "${fatherList.size} ${resources.getString(R.string.replies)}"
                    fatherList.forEach { f ->
                        val list = ArrayList<ReplyDetail>()
                        childList.forEach { c ->
                            if (f.commentsReplyPk == c.commentsReplyRootPk) {
                                if (list.size > 2) {
                                    f.hasMoreReplyComment = true
                                } else {
                                    f.hasMoreReplyComment = false
                                    list.add(
                                        ReplyDetail(
                                            replyAvatarUrl = c.replyAvatarUrl,
                                            delFlag = c.delFlag,
                                            replyContent = c.replyContent,
                                            replyDatetime = c.replyDatetime,
                                            commentsReplyRootPk = c.commentsReplyRootPk,
                                            commentsPk = c.commentsPk,
                                            commentsReplyPk = c.commentsReplyPk,
                                            replySourceUserName = c.replySourceUserName,
                                            replySourceUserPk = c.replySourceUserPk,
                                            replySourcePk = c.replySourceUserPk,
                                            replyName = c.replyName,
                                            replyUserPk = c.replyUserPk
                                        )
                                    )
                                }
                            }
                        }
                        f.replyComment = list
                    }
                    mReplyList.clear()
                    mStationViewModel.listCommentReply.value?.clear()
                    mReplyList.addAll(fatherList)
                    mStationViewModel.listCommentReply.value = mReplyList
                    mStationViewModel.commentCount.value = mReplyList.size
                    closeKeyWord()
                }

//                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<List<Comment>>> {
                return SingletonFactory.apiService.getCommentReply(request)
            }
        }
    }

    fun delReply(pk: String) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                getData()
                ToastUtils.showShort(getString(R.string.success))

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delReply(
                    Comment(
                        commentsReplyPk = pk,
                        check = null
                    )
                )
            }
        }
    }

    fun delComment(pk: String) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
               // getData()
                LiveBus.getInstance().post(EventBean(Configs.NOTIFICATION_MSG, true, ""))

                this@CommentDetailActivity.finish()
                ToastUtils.showShort(getString(R.string.success))

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delCOmment(
                    Comment(
                        appCommentsPk = pk,
                        check = null
                    )
                )
            }
        }
    }

    fun addRep() {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                dismissLoading()
                getData()
                ToastUtils.showShort(getString(R.string.success))
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
        var pk = ""
        /*         val request = if(comment?.appCommentsPk.isNullOrBlank())
              pk = comment?.commentsReplyPk!!
           else
              pk = comment?.appCommentsPk!!*/
        if (commentsReplyPk == null) {
            pk = appCommentsPk!!
        } else {
            pk = commentsReplyPk as String
        }

        Log.e("hm---pk", pk.toString())
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {
            override fun onSuccess(data: String?) {
                Log.e("hm--------", "成功")
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