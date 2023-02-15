package com.evport.businessapp.ui.page.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
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
import com.evport.businessapp.utils.FileUtils
import com.evport.businessapp.utils.SpaceItemDecoration
import com.evport.businessapp.utils.loader.Glide4Engine
import com.evport.businessapp.utils.todp
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_create_feedback.*
import kotlinx.android.synthetic.main.activity_create_feedback.back
import kotlinx.android.synthetic.main.fragment_user_setting.*
import java.io.File

class CreatFeedbackActivity : BaseActivity() {


    private lateinit var mStationViewModel: StationViewModel
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_create_feedback, mStationViewModel)
    }

    override fun initViewModel() {

        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }


    val images = mutableListOf<File>()
    val imageList = mutableListOf<Uri>()
    val StringList = mutableListOf<SocketType>()


    var mImageAdapter: ImageAddAdapter? = null
    var reason = ""
    lateinit var add: Uri

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_create_feedback)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()
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

                images.remove(File(item.toString()))
                imageList.remove(item)
                if (!imageList.contains(add) && imageList.size < 3)
                    imageList.add(add)
                submitList(imageList)
            }
            setOnItemClickListener { item, position ->
                if (position == imageList.size - 1 && images.size < 3 && imageList.size < 4) {
                    requestPermission()
                }
            }
        }
        var mStringAdapter = ListStringAdapter(context = baseContext)

//

//        列表
//        StringList.add(SocketType("", "device failure"))
//        StringList.add(SocketType("", "App information is wrong"))
//        StringList.add(SocketType("", "problem encountered in payment"))
//        StringList.add(SocketType("", "App is unstabl"))
//        StringList.add(SocketType("", "Account related issues"))
//        StringList.add(SocketType("", "Other problems"))
        StringList.add(SocketType("", this.resources.getString(R.string.device_failure)))//1
        StringList.add(SocketType("", this.resources.getString(R.string.Appisunstabl)))//4
        StringList.add(SocketType("", this.resources.getString(R.string.Appinformation)))//2
        StringList.add(SocketType("", this.resources.getString(R.string.issues)))//5
        StringList.add(SocketType("", this.resources.getString(R.string.problemencountered)))//3
        StringList.add(SocketType("", this.resources.getString(R.string.Otherproblems)))//6



        recycler_view_product.apply {
            adapter = mStringAdapter.apply {
                setOnItemClickListener { item, position ->
                    this.selectId = position
                    reason = item.name
                    notifyDataSetChanged()
                    checkStatus()
                }
            }
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)

        }
        mStringAdapter.submitList(StringList)


//        图片列表
        imageList.add(add)

        recycler_view_image.apply {
            adapter = mImageAdapter
            var layoutManager =
                LinearLayoutManager(this@CreatFeedbackActivity)
            //设置布局管理器
            //设置布局管理器
            recycler_view_image.setLayoutManager(layoutManager)
            //设置为垂直布局，这也是默认的
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

                list.add(FileUtils.getBitmapFormUri(baseContext, it))
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
                if (!message.isNullOrBlank()){
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
        btn_confrim.isEnabled = reason.trim().isNotEmpty() && et_reason.text.toString().trim().isNotEmpty()
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
                            this@CreatFeedbackActivity,
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
//                    activity.toast("Should open READ_EXTERNAL_STORAGE permission")
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
        val maxSize = 3 - images.size

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
                images.add(f)
//                compressBmpToFile(s,f)
                imageList.add(s)
            }
        }

        if (images.size < 3 && !imageList.contains(add)) {
            imageList.add(add)
        }
        mImageAdapter?.submitList(imageList)
    }


}
