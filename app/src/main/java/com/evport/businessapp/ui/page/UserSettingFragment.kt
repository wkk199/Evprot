package com.evport.businessapp.ui.page

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.ToastUtils
import com.gyf.immersionbar.ImmersionBar
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.kunminx.architecture.utils.SPUtils
import com.lxj.xpopup.XPopup
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
import com.evport.businessapp.ui.view.EmailPicker
import com.evport.businessapp.ui.view.NamePicker
import com.evport.businessapp.ui.view.NoticePicker
import com.evport.businessapp.utils.FileUtils
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.loader.Glide4Engine
import com.evport.businessapp.utils.saveUser
import com.evport.businessapp.utils.setImageIsWifi
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_user_setting.iv_avatar
import kotlinx.android.synthetic.main.fragment_user_setting.tv_email
import kotlinx.android.synthetic.main.fragment_user_setting.tv_name
import java.io.File

/**
 * Create by KunMinX at 19/10/29
 */
class UserSettingFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .init()
        requireContext().getUser()?.apply {
            iv_avatar.setImageIsWifi(avatarUrl)
        }
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

            requestPermission()

        }

        fun password() {
            nav().navigate(R.id.action_global_mainFragment_to_changePasswordFragment)
        }

        fun chageEmail() {
            var email = ""
            if (requireContext().getUser()!!.email != null) {
                email = requireContext().getUser()!!.email.toString()
            }
            XPopup.Builder(requireContext())
                .asCustom(
                    EmailPicker(
                        requireContext(),
                        email
                    ).apply {
                        setCallBack(
                            okBlock = {
                                updateInfo(it, 1)

                            }
                        )
                    })
                .show()
        }

        fun chageName() {
            XPopup.Builder(requireContext())
                .asCustom(
                    NamePicker(
                        requireContext(),
                        requireContext().getUser()!!.name!!
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
                "注销",
                "确认注销我的帐户和所有相关数据",
                "确认",
                "取消"
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
    private fun requestPermission() {

        RxPermissions(requireActivity())
            .request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .subscribe {
                if (it) {
                    selectPicture()

                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        )
                    ) {
                        try {
                            ToastUtils.showShort(resources.getString(R.string.weneedpermission))
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

                        setAvatar()
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


    fun setAvatar() {

        showLoading()
        val s = FileUtils.getBitmapFormUri(requireContext(), uri)
        if (s.isBlank()) {
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
                return SingletonFactory.apiService.updateAvatar(Image(s))
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
                ToastUtils.showShort("修改成功")
                dismissLoading()
                val user = requireContext().getUser()
                user?.apply {
                    if (type == 1) {
                        user.email = email
                    } else {
                        user.name = email
                    }

                }
                requireContext().saveUser(user!!)
                if (type == 1) {
                    tv_email.text = email
                } else {
                    tv_name.text = email
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
                if (type == 1) {
                    user!!.email = email
                } else {
                    user!!.name = email
                }
                user!!.sex = null
                user.ssoticket = null
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