package com.evport.businessapp.ui.page.activity

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.FeedbackCommit
import com.evport.businessapp.data.bean.SocketType
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.ImageAddAdapter
import com.evport.businessapp.ui.page.adapter.ListStringAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.ui.view.PopAvatarPicker
import com.evport.businessapp.utils.*
import com.evport.businessapp.utils.loader.Glide4Engine
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.lxj.xpopup.XPopup
import com.nkr.home.ui.imgselector.WeChatPresenter
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.selectconfig.CropConfig
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_create_feedback.*
import kotlinx.android.synthetic.main.activity_create_feedback.back
import kotlinx.android.synthetic.main.fragment_user_setting.*
import java.io.File
import java.net.URL

class CreatFeedbackActivity : BaseActivity() {


    private lateinit var mStationViewModel: StationViewModel


    //头像选择弹窗
    private val mPopAvatarPicker by lazy { PopAvatarPicker(this) }


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_create_feedback, mStationViewModel)
    }

    override fun initViewModel() {

        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    //裁剪配置
    private val cropConfig by lazy {
        CropConfig()
            .apply {
                cropRectMargin = 100
                saveInDCIM(false)
                isCircle = false
                cropStyle = CropConfig.STYLE_GAP
                cropGapBackgroundColor = Color.WHITE
            }

    }

    val imageList = mutableListOf<Uri>()
    val StringList = mutableListOf<SocketType>()


    var mImageAdapter: ImageAddAdapter? = null
    var reason = ""
    lateinit var add: Uri

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_create_feedback)
        add = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + resources.getResourcePackageName(R.drawable.image_add) + "/"
                    + resources.getResourceTypeName(R.drawable.image_add) + "/"
                    + resources.getResourceEntryName(R.drawable.image_add)
        )

        back.setOnClickListener {
            finish()
        }
        mImageAdapter = ImageAddAdapter(context = baseContext).apply {
            delClick = { item ->

                imageList.remove(item)
                if (!imageList.contains(add) && imageList.size < 3)
                    imageList.add(add)
                submitList(imageList)
            }
            setOnItemClickListener { item, position ->


                if (imageList.size < 4 && Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                + resources.getResourcePackageName(R.drawable.image_add) + "/"
                                + resources.getResourceTypeName(R.drawable.image_add) + "/"
                                + resources.getResourceEntryName(R.drawable.image_add)
                    ).toString().contains(Uri.parse(imageList[position].path).toString())){


                    requestPermission()

                }

//                if (position == imageList.size - 1 && imageList.size < 4 && !Uri.parse(
//                        ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//                                + resources.getResourcePackageName(R.drawable.image_add) + "/"
//                                + resources.getResourceTypeName(R.drawable.image_add) + "/"
//                                + resources.getResourceEntryName(R.drawable.image_add)
//                    ).toString().contains(Uri.parse(imageList[position].path).toString())
//                ) {
//                    requestPermission()
//                }
            }
        }
        var mStringAdapter = ListStringAdapter(context = baseContext)


        mPopAvatarPicker.setCallBack(object : PopAvatarPicker.CallBack {
            //相机
            override fun clickCamera() {
                ImagePicker
                    .takePhotoAndCrop(
                        this@CreatFeedbackActivity,
                        WeChatPresenter(),
                        cropConfig
                    ) {
                        //拍照回调，主线程
                        //图片选择回调，主线程
                        it.first().cropUrl.toString().toast()
                        imageList.add(
                            imageList.size - 1,
                            Uri.parse(it.first().cropUrl)
                        )
                        if (imageList.size >= 4 && imageList.contains(add)) {
                            imageList.removeLast()
                        }
                        mImageAdapter?.submitList(imageList)
                        mPopAvatarPicker.dismiss()
                    }
            }

            //相册
            override fun clickPhoto() {
                ImagePicker
                    .withMulti(WeChatPresenter())//指定presenter
                    // 设置要加载的文件类型，可指定单一类型
                    .mimeTypes(MimeType.ofImage())
                    //设置需要过滤掉加载的文件类型
                    .filterMimeTypes(MimeType.GIF)
                    .setMaxCount(4 - imageList.size)
                    .pick(this@CreatFeedbackActivity) {
                        it.forEach {
                            Log.e("TAG", "clickCamera: --------"+it.path )

                            imageList.add(
                                imageList.size - 1,
                                Uri.parse(it.path)
                            )
                            if (imageList.size >= 4 && imageList.contains(add)) {
                                imageList.removeLast()
                            }

                            mImageAdapter?.submitList(imageList)
                            mPopAvatarPicker.dismiss()
                        }
                    }
            }
        })

//

//        列表
//        StringList.add(SocketType("", "device failure"))
//        StringList.add(SocketType("", "App information is wrong"))
//        StringList.add(SocketType("", "problem encountered in payment"))
//        StringList.add(SocketType("", "App is unstabl"))
//        StringList.add(SocketType("", "Account related issues"))
//        StringList.add(SocketType("", "Other problems"))
        StringList.apply {
            add(SocketType("", getString(R.string.device_failure)))
            add(SocketType("", getString(R.string.problemencountered)))
            add(SocketType("", getString(R.string.issues)))
            add(SocketType("", getString(R.string.Appinformation)))
            add(SocketType("", getString(R.string.Otherproblems)))
            add(SocketType("", getString(R.string.Appisunstabl)))
        }
//        StringList.add(SocketType("", this.resources.getString(R.string.device_failure)))//1
//        StringList.add(SocketType("", this.resources.getString(R.string.Appisunstabl)))//4
//        StringList.add(SocketType("", this.resources.getString(R.string.Appinformation)))//2
//        StringList.add(SocketType("", this.resources.getString(R.string.issues)))//5
//        StringList.add(SocketType("", this.resources.getString(R.string.problemencountered)))//3
//        StringList.add(SocketType("", this.resources.getString(R.string.Otherproblems)))//6


        recycler_view_product.apply {
            adapter = mStringAdapter.apply {
                setOnItemClickListener { item, position ->
                    this.selectId = position
                    reason = item.name
                    notifyDataSetChanged()
                    checkStatus()
                }
            }
            layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL)

        }
        mStringAdapter.submitList(StringList)


//        图片列表
        imageList.add(add)

        recycler_view_image.apply {
            adapter = mImageAdapter
            var layoutManager =
                LinearLayoutManager(this@CreatFeedbackActivity)
            //设置布局管理器
            recycler_view_image.setLayoutManager(layoutManager)
            //设置为垂直布局，这也是默认的
            layoutManager.orientation = OrientationHelper.HORIZONTAL
            // layoutManager = GridLayoutManager(this@CreatFeedbackActivity, 3, RecyclerView.VERTICAL, false)
        }
        recycler_view_image.addItemDecoration(
            SpaceItemDecoration(
                10.todp(),
                ContextCompat.getColor(this, android.R.color.transparent),
                SpaceItemDecoration.VERTICAL_LIST,
                0, 0,
                SpaceItemDecoration.TYPE_ALL
            )
        )
        mImageAdapter?.submitList(imageList)
        checkStatus()
        et_reason.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkStatus()
                val s = et_reason.text.toString().length
                tv_num.text = "$s/500"
            }

        })

        btn_confrim.setOnClickListener {

            val list = ArrayList<String>()
            imageList.remove(add)
            imageList.forEach {
                list.add(FileUtils.getBitmapFormUri(baseContext, Uri.fromFile(File(it.path!!))))
            }


            toCommit(list)

        }
    }


    fun toCommit(list: List<String>) {
        showLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                finish()

            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.feedbackCommit(
                    FeedbackCommit(
                        list,
                        et_reason.text.toString().trim(),
                        reason
                    )
                )
            }
        }
    }


    fun checkStatus() {
        btn_confrim.isEnabled =
            reason.trim().isNotEmpty() && et_reason.text.toString().trim().isNotEmpty()
        /*  if (btn_confrim.isEnabled){
              btn_confrim.setBackgroundColor(resources.getColor(R.color.colorTheme))
          }else {
              btn_confrim.setBackgroundColor(resources.getColor(R.color.light_text_color))
          }*/
    }


    /**==============**/

    private fun requestPermission() {
        XXPermissions.with(this)
            .permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.CAMERA)

            .request(object : OnPermissionCallback {

                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (!all) {
                        "Please obtain permission to select images".toast()
                        return
                    }
//                    selectPicture()
                    XPopup.Builder(this@CreatFeedbackActivity)
                        .asCustom(mPopAvatarPicker)
                        .show()

                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
                        "Permanently denied authorization, please manually grant permission to select images".toast()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(
                            this@CreatFeedbackActivity,
                            permissions
                        )
                    } else {
                        "Failed to get the selected image".toast()
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


    private fun selectPicture() {
        val maxSize = 3 - imageList.size

        EasyPhotos.createAlbum(
            this,
            true,
            false,
            Glide4Engine()
        )//参数说明：上下文，是否显示相机按钮，是否使用宽高数据（false时宽高数据为0，扫描速度更快），[配置Glide为图片加载引擎](https://github.com/HuanTanSheng/EasyPhotos/wiki/12-%E9%85%8D%E7%BD%AEImageEngine%EF%BC%8C%E6%94%AF%E6%8C%81%E6%89%80%E6%9C%89%E5%9B%BE%E7%89%87%E5%8A%A0%E8%BD%BD%E5%BA%93)
            .setCount(maxSize)
            .setPuzzleMenu(false)
            .setCleanMenu(false)
            .setFileProviderAuthority("com.evport.businessapp.fileprovider")//参数说明：见下方`FileProvider的配置`
            .start(object : SelectCallback() {
                override fun onResult(pathList: java.util.ArrayList<Photo>?, isOriginal: Boolean) {
                    handleImageResult(pathList?.map { it.uri })
                }

                override fun onCancel() {

                }

            })
    }

    private fun handleImageResult(pathList: List<Uri>?) {
        pathList?.apply {
            imageList.remove(add)
            pathList.forEachIndexed { index, s ->
                val f = File(s.toString())
                val bitmap = BitmapFactory.decodeStream(
                    contentResolver.openInputStream(s)
                )
//                imageList.add(f)
//                compressBmpToFile(s,f)
                imageList.add(s)
            }
        }

        if (imageList.size < 3 && !imageList.contains(add)) {
            imageList.add(add)
        }
        mImageAdapter?.submitList(imageList)
    }


}
