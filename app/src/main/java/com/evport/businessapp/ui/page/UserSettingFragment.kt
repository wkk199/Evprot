package com.evport.businessapp.ui.page

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.App.Companion.appContext
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.DeleteUserReq
import com.evport.businessapp.data.bean.Image
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.ui.view.*
import com.evport.businessapp.utils.*
import com.evport.businessapp.utils.loader.Glide4Engine
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.kunminx.architecture.utils.SPUtils
import com.lxj.xpopup.XPopup
import com.nkr.home.ui.imgselector.WeChatPresenter
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.selectconfig.CropConfig
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home_user.*
import kotlinx.android.synthetic.main.fragment_user_setting.*
import kotlinx.android.synthetic.main.fragment_user_setting.iv_avatar
import java.io.File

/**
 * Create by KunMinX at 19/10/29
 */
class UserSettingFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null

    //头像选择弹窗
    private val mPopAvatarPicker by lazy { PopAvatarPicker(context!!) }

    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig { //TODO tip: DataBinding 严格模式：

        return DataBindingConfig(R.layout.fragment_user_setting, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.info, requireContext().getUser())
    }

    //裁剪配置
    private val cropConfig by lazy {
        CropConfig()
            .apply {
                cropRectMargin = 100
                saveInDCIM(false)
                isCircle = true
                cropStyle = CropConfig.STYLE_GAP
                cropGapBackgroundColor = Color.WHITE
            }

    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
//        ImmersionBar.with(this)
//            .navigationBarColor(R.color.black)
//            .keyboardEnable(false)
//            .init()
        requireContext().getUser()?.apply {
            iv_avatar.setImageIsWifi(avatarUrl)
        }



        mPopAvatarPicker.setCallBack(object : PopAvatarPicker.CallBack {
            //点击了相机
            override fun clickCamera() {
                mPopAvatarPicker.dismiss()
                XXPermissions.with(this@UserSettingFragment)
                    .permission(Permission.CAMERA)
                    .permission(Permission.READ_EXTERNAL_STORAGE)
                    .permission(Permission.WRITE_EXTERNAL_STORAGE)

                    .request(object : OnPermissionCallback {

                        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                            if (!all) {
                                ToastUtils.showShort("Please obtain permission to select images")
                                return
                            }
                            ImagePicker
                                .takePhotoAndCrop(
                                    activity,
                                    WeChatPresenter(),
                                    cropConfig
                                ) {
                                    //拍照回调，主线程
                                    //图片选择回调，主线程

                                    var uri = FileUtils.getBitmapFormUri(
                                        appContext,
                                        Uri.fromFile(File(it.first().cropUrl))
                                    )
                                    setAvatar(uri)
                                }
                        }

                        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                            if (never) {
                                ToastUtils.showShort("Permanently denied authorization, please manually grant permission to select images")
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(
                                    this@UserSettingFragment,
                                    permissions
                                )
                            } else {
                                ToastUtils.showShort("Failed to get the selected image")
                            }
                        }
                    })
            }

            //点击了图库
            override fun clickPhoto() {
                mPopAvatarPicker.dismiss()

                XXPermissions.with(this@UserSettingFragment)
                    .permission(Permission.CAMERA)
                    .permission(Permission.READ_EXTERNAL_STORAGE)
                    .permission(Permission.WRITE_EXTERNAL_STORAGE)

                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                            if (!all) {
                                ToastUtils.showShort("Please obtain permission to select images")
                                return
                            }
                            ImagePicker
                                .withMulti(WeChatPresenter())//指定presenter
                                .cropAsCircle()
                                .crop(activity) {
                                    val uri = FileUtils.getBitmapFormUri(
                                        appContext,
                                        Uri.fromFile(File(it.first().cropUrl))
                                    )
                                    setAvatar(uri)
//                                    mViewModel.updateAvatar(uri)
                                }
                        }

                        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                            if (never) {
                                ToastUtils.showShort("Permanently denied authorization, please manually grant permission to select images")
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(
                                    this@UserSettingFragment,
                                    permissions
                                )
                            } else {
                                ToastUtils.showShort("Failed to get the selected image")
                            }
                        }
                    })
            }
        })


    }

    public override fun loadInitData() {
        super.loadInitData()
    }

    inner class ClickProxy {
        fun logoutClick() {
            logOut()
            sharedViewModel.isLoginSuccess.postValue(false)
        }

        fun back() {
            nav().navigateUp()
        }

        fun avatar() {
            XPopup.Builder(context)
                .asCustom(mPopAvatarPicker)
                .show()

        }

        fun password() {
            nav().navigate(R.id.action_global_mainFragment_to_changePasswordFragment)
        }



        fun phone() {
            XPopup.Builder(requireContext())
                .asCustom(
                    PonePicker(
                        requireContext(),
                        requireContext().getUser()!!.phone!!
                    ).apply {
                        setCallBack(
                            okBlock = {
                                updateInfo(it, 1)

                            }
                        )
                    })
                .show()
        }

        fun sex() {
            XPopup.Builder(requireContext())
                .asCustom(
                    SexPicker(
                        requireContext(),
                        requireContext().getUser()!!.sex!!
                    ).apply {
                        setCallBack(
                            okBlock = {
                                updateInfo(it, 2)

                            }
                        )
                    })
                .show()
        }

        fun delete() {
            var noticeView = NoticePicker(
                requireActivity(),
//                "注销",
//                "确认注销我的帐户和所有相关数据",
//                "确认",
//                "取消"
            )
            XPopup.Builder(activity)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .asCustom(noticeView)
                .show()
            noticeView.setCallBack {
                deleteUser()
            }

        }

    }


    @SuppressLint("CheckResult")
//    private fun requestPermission() {
//
//        RxPermissions(requireActivity())
//            .request(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//            )
//            .subscribe {
//                if (it) {
//                    selectPicture()
//
//                } else {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(
//                            requireActivity(),
//                            Manifest.permission.CAMERA
//                        )
//                    ) {
//                        try {
//                            ToastUtils.showShort(resources.getString(R.string.weneedpermission))
//                        } catch (e: Exception) {
//
//                        }
//                    } else {
//                        val intent = Intent(
//                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.fromParts("package", requireActivity().packageName, null)
//                        )
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
//                    }
//                }
//            }
//
//    }

    private fun selectPicture() {
        EasyPhotos.createAlbum(this, true, false, Glide4Engine())
            .setPuzzleMenu(false)
            .setCleanMenu(false)
            .setFileProviderAuthority("com.evport.businessapp.fileprovider")//参数说明：见下方`FileProvider的配置`
            .start(object : SelectCallback() {
                override fun onResult(pathList: java.util.ArrayList<Photo>?, isOriginal: Boolean) {

                    if (pathList?.size!! > 0) {
                        images = File(pathList[0].toString())
                        uri = pathList[0].uri
                        iv_avatar.setImageIsWifi(pathList[0].uri)

//                        setAvatar()
                    } else {
                        ToastUtils.showShort(R.string.toast_not_select_image)
                    }

                }

                override fun onCancel() {

                }

            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    var images: File? = null
    var uri: Uri? = null


    fun setAvatar(uri: String) {

        showLoading()
//        val s = FileUtils.getBitmapFormUri(requireContext(), uri)
        if (uri.isBlank()) {
            ToastUtils.showShort(getString(R.string.somthingerror))
            return
        }
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {

                ToastUtils.showShort(getString(R.string.success))
                dismissLoading()
                val user = requireContext().getUser()
                user?.apply {
                    user.avatarUrl = data?.avatarUrl

                }
                iv_avatar.setImageIsWifi(data?.avatarUrl)
                requireContext().saveUser(user!!)
                sharedViewModel.isLoginSuccess.postValue(true)

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

                dismissLoading()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.updateAvatar(Image(uri))
            }
        }
    }

    fun logOut() {
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    message.toast()
                }
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.logout(
                    user = User(
                        account = SPUtils.getInstance()
                            .getString(Configs.EMAIL)
                    )
                )
            }
        }
    }

    fun updateInfo(email: String, type: Int) {
        showLoading()
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {
            override fun onSuccess(data: String?) {
                ToastUtils.showShort(getString(R.string.success))
                dismissLoading()
                val user = requireContext().getUser()
                user?.apply {
                    when (type) {
                        1 -> {
                            user.phone = email
                        }
                        2 -> {
                            user.sex = email
                        }
                    }

                }
                requireContext().saveUser(user!!)
                when (type) {
                    1 -> {
                        tv_phone.text = email
                    }
                    2 -> {
                        tv_sex.text = email
                    }
                }

                sharedViewModel.isLoginSuccess.postValue(true)
            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()) {
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                val user = requireContext().getUser()
                when (type) {
                    1 -> {
                        user!!.phone = email
                    }
                    3 -> {
                        user!!.sex = email
                    }
                }
                user!!.ssoticket = null
                user.userPk = null
                return SingletonFactory.apiService.updateInfo(user)
            }
        }
    }

    fun deleteUser() {
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {
            override fun onSuccess(data: String?) {
                sharedViewModel.isLoginSuccess.postValue(false)
            }

            override fun onFailure(message: String) {
                ToastUtils.showShort(message)
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.deleteUser(DeleteUserReq(name = requireContext().getUser()!!.name))
            }
        }
    }
}