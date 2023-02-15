package com.evport.businessapp.ui.page.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.NotificationFeedbackDetailAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.utils.*
import com.evport.businessapp.utils.loader.Glide4Engine
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.previewlibrary.GPreviewBuilder
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_noti_feedback_detail.*
import java.util.*
import kotlin.collections.ArrayList

class NotiFeedbackDetailActivity : BaseActivity() {

 /*   private val feedback by lazy {
        intent.getParcelableExtra("feedback") as Feedback?

    }*/
    var feedback:Feedback?=null
    private var mDrawerViewModel: DrawerViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val feedbacks=intent.getStringExtra("feedback")
        feedback=Gson().fromJson(feedbacks,Feedback::class.java)
        (selle_recycler_view1.getItemAnimator() as SimpleItemAnimator).setSupportsChangeAnimations(false)
        // adapter.setHasStableIds(true);
        if (feedback == null)
           this.finish()
        // adddata()
        Log.e("hm----feedback",Gson().toJson(feedback))
        getData(0)
        feedbackReplyCheck()
        tv_num.isVisible = imageList.size > 0
        refreshLayout.isRefreshing = false
        refreshLayout.setOnRefreshListener {
            pageNumber--
            canLoadMore = true
            getData(3)
            refreshLayout.isRefreshing = false
        }


        selle_recycler_view1.addOnScrollListener(object : onLoadMoreListener() {
            override fun onLoading(countItem: Int, lastItem: Int) {
                if (canLoadMore) {
                    getData(4)
                }

            }
        })
    }

    override fun initViewModel() {
        mDrawerViewModel = getActivityViewModel(DrawerViewModel::class.java)
    }
    lateinit var adapter: NotificationFeedbackDetailAdapter
    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = NotificationFeedbackDetailAdapter(this).apply {

            recyClick = { item, position ->
                val list = item?.imageList()
                val arrayList = ArrayList<ImageUrl>()
                list?.forEach {
                    arrayList.add(ImageUrl(it))
                }
                showBigImg(arrayList, position)
            }

        }
        return DataBindingConfig(R.layout.activity_noti_feedback_detail, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, adapter)
    }

    fun adddata() {
        allList.add(Feedback(feedbackContent = getString(R.string.as_you_know)))
        feedback?.let { allList.add(it) }
    }

    var canLoadMore = true
    var pageNumber = 0
    var allList = ArrayList<Feedback>()
    var realList = ArrayList<Feedback>()

    fun getData(type: Int) {
        showLoading()
        object : NetworkBoundResource<FeedBackDataBean>(networkStatusCallback = object :
            NetworkStatusCallback<FeedBackDataBean> {

            override fun onSuccess(data1: FeedBackDataBean?) {
                dismissLoading()

                var data = data1!!.feedbackData;
                Log.e("hm----",Gson().toJson(data))

                data?.apply {
                    if (pageNumber == 0 || type == 1) {
                        if (data != null && data.size != 0) {
                            var temporaryList = ArrayList<Feedback>()
                            for (index in 0 until data.size) {
                                var iss = true
                                for (i in 0 until realList.size) {
                                    if (realList[i].replyFeedbackPk.equals(data[index].replyFeedbackPk)) {
                                        iss = false;
                                        continue
                                    }
                                }
                                if (iss) {
                                    temporaryList.add(data[index])
                                }
                            }
                            realList.addAll(0, temporaryList)
                        }

                    } else {
                        if (data != null && data.size != 0) {
                            var temporaryList = ArrayList<Feedback>()
                            for (index in 0 until data.size) {
                                var iss = true
                                for (i in 0 until realList.size) {
                                    if (realList[i].replyFeedbackPk.equals(data[index].replyFeedbackPk)) {
                                        iss = false;
                                        continue
                                    }
                                }
                                if (iss) {
                                    temporaryList.add(data[index])
                                }
                            }
                            realList.addAll(temporaryList)
                        }
                    }
                    var reverseList = ArrayList<Feedback>()
                    reverseList.addAll(realList)
                    Collections.reverse(reverseList); // 倒序排列
                    allList.clear()
                    if (data == null || data.size == 0 || data1.length < 8) {
                        adddata()
                    }

                    allList.addAll(reverseList)
                    var fastTime = ""
                    for (index in 0 until allList.size) {
                        if (index != 0) {
                            if (DateUtil.stringToLong(
                                    allList.get(index).commentTime()
                                ) - DateUtil.stringToLong(fastTime) > 1000 * 60 * 5
                            ) {
                                allList.get(index).isShowTime = true
                                fastTime = allList.get(index).commentTime()
                            } else {
                                allList.get(index).isShowTime = false
                            }
                        } else {
                            fastTime = allList.get(index).commentTime()
                        }
                    }
Log.e("hm----allList",Gson().toJson(allList))
                    mDrawerViewModel?.listNotiFeedback?.value = allList
                    for (item in 0 until allList.size) {
                        Log.e("test111", "$item  ${Gson().toJson(feedback?.imageList())}")
                    }
                    if (type != 3) {

                        handler.postDelayed(runnable, 500);//每两秒执行一次runnable.
                    }
                    //  adapter.notifyDataSetChanged()

                }
                canLoadMore = !data.isNullOrEmpty()

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<FeedBackDataBean>> {
                return SingletonFactory.apiService.getNotiFeedbackDetail(

                    if (type == 1 || type == 4) {
                        StationCommentReq(pageNum = 0, feedbackPk = feedback?.feedbackPk)
                    } else {
                        StationCommentReq(pageNum = pageNumber, feedbackPk = feedback?.feedbackPk)
                    }

                )
            }
        }
    }


    inner class ClickProxy {
        fun back() {
           this@NotiFeedbackDetailActivity.finish()
        }

        @SuppressLint("ServiceCast")
        fun send() {
            if (et_reply.text.isNullOrBlank() && (imageList == null || imageList.size == 0)) {
                showLongToast(R.string.please_input)
                return
            }
            showLoading()
            val list = ArrayList<String>()
            imageList.forEach {
                list.add(FileUtils.getBitmapFormUri(this@NotiFeedbackDetailActivity, it))
            }

            toCommit(list)
            val imm =
                this@NotiFeedbackDetailActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(tv_send.windowToken, 0)
        }

        fun openPhoto() {
            requestPermission()
        }
    }

    fun clear() {
        dismissLoading()
        et_reply.setText("")
        imageList.clear()
        tv_num.text = ""
        tv_num.isVisible = imageList.size > 0
    }

    fun toCommit(list: List<String>) {
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                clear()

                getData(1)


            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.feedbackReply(
                    FeedbackReplyCommit(
                        img = list,
                        content = et_reply.text.toString(),
                        feedbackPk = feedback?.feedbackPk.toString()
                    )
                )
            }
        }
    }


    /**==============**/

    private fun requestPermission() {


        XXPermissions.with(this)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.CAMERA)

            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (!all) {
                        ToastUtils.showShort("请获取选择图片权限")
                        return
                    }
                    selectPicture()
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        ToastUtils.showShort("被永久拒绝授权，请手动授予选择图片权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(
                            this@NotiFeedbackDetailActivity,
                            permissions
                        )
                    } else {
                        ToastUtils.showShort("获取选择图片失败")
                    }
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) { //CAMERA 请求代码匹配结果
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectPicture()
            } else {
            }
            return
        }
    }

    private fun isPermissionGranted(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setPermission(
        context: Context, activity: Activity, permission: String,
        requestCode: Int
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager
                    .PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    ToastUtils.showShort(resources.getString(R.string.weneedpermission))
                    return false
                }
                if (permission.contains(",")) {
                    val list = permission.split(",")
                    ActivityCompat.requestPermissions(activity, list.toTypedArray(), requestCode)
                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
                }
                return true
            }
            return true
        }
        return false
    }

    private fun selectPicture() {
        imageList.clear()
        val maxSize = 3 - imageList.size
//        Matisse.from(this)
//            .choose(setOf(MimeType.PNG, MimeType.JPEG, MimeType.JPG), true)
//            .countable(true)
//            .capture(true)
//            .captureStrategy(
//                CaptureStrategy(true, "com.evport.businessapp.fileprovider", "test")
//            )
//            .maxSelectable(maxSize)
//            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//            .imageEngine(Glide4Engine())    // for glide-V4
//            .originalEnable(true)
//            .maxOriginalSize(20)
//            .forResult(101)

        EasyPhotos.createAlbum(this, true,false, Glide4Engine())//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
            .setCount(maxSize)
            .setPuzzleMenu(false)
            .setCleanMenu(false)
            .setFileProviderAuthority("com.evport.businessapp.fileprovider")//参数说明：见下方`FileProvider的配置`
            .start(object : SelectCallback() {
                override fun onResult(pathList: java.util.ArrayList<Photo>?, isOriginal: Boolean) {
                    pathList?.apply {
                        pathList.forEachIndexed { index, s ->
                            imageList.add(s.uri)
                        }
                        tv_num.text = "" + imageList.size
                        tv_num.isVisible = imageList.size > 0
                    }

                }

                override fun onCancel() {

                }

            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            101 -> handleImageResult(data)
        }
    }

    val imageList = mutableListOf<Uri>()

//    private fun handleImageResult(data: Intent) {
//        val pathList = Matisse.obtainResult(data)
//        pathList?.apply {
//            pathList.forEachIndexed { index, s ->
//                imageList.add(s)
//            }
//        }
//        tv_num.text = "" + imageList.size
//        tv_num.isVisible = imageList.size > 0
//    }

    fun showBigImg(list: ArrayList<ImageUrl>, position: Int) {
        //打开预览界面
        GPreviewBuilder.from(this)
            .setData(list)
            .setCurrentIndex(position)
            .setSingleFling(true)
            .setType(GPreviewBuilder.IndicatorType.Number)
            // 小圆点
//  .setType(GPreviewBuilder.IndicatorType.Dot)
            .start()//启动
    }

    val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {

            super.handleMessage(msg)
            selle_recycler_view1.scrollToPosition(allList.size - 1)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(0)
    }
    var runnable: Runnable = object : Runnable {
        override fun run() {
// TODO Auto-generated method stub
            //要做的事情
            //   handler.postDelayed(this, 2000)
            val message = Message()
            message.what = 1
            handler.sendMessage(message) //发送消息

        }
    }

    fun feedbackReplyCheck() {
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
                return SingletonFactory.apiService.feedbackReplyCheck(
                    MessageData(feedbackPk = feedback?.feedbackPk)
                )
            }
        }
    }
}