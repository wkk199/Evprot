package com.evport.businessapp.ui.page

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.CacheDiskStaticUtils
import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.startUpdateFlowForResult
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.DeleteUserReq
import com.evport.businessapp.data.bean.NameVo
import com.evport.businessapp.data.bean.User
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.activity.ChangeLanguageActivity
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.ui.view.NoticePicker
import com.evport.businessapp.upgrade.*
import com.evport.businessapp.upgrade.model.bean.UpgradeOptions
import com.evport.businessapp.utils.*
import com.kunminx.architecture.utils.SPUtils
import com.lxj.xpopup.XPopup

import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_app_setting.*
import org.jetbrains.anko.support.v4.startActivity
import java.io.File
import java.math.BigDecimal


/**
 * Create by KunMinX at 19/10/29
 */
class AppSettingFragment : BaseFragment() {
    private var mDrawerViewModel: DrawerViewModel? = null
    private var manager: UpgradeManager? = null
    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0x8052
    var  url=""
    override fun initViewModel() {
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)
    }

    var isWifi = false
    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_app_setting, mDrawerViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        manager = UpgradeManager(activity)
    }

    public override fun loadInitData() {
        super.loadInitData()
        isWifi = requireContext().getIsWifiLoad()
        changeWifi()
        mDrawerViewModel?.cache?.set(getCacheSize(requireContext()))
        mDrawerViewModel?.version?.set(AppUtils.getAppVersionName())
    }

    fun changeWifi() {
        if (isWifi) {
            iv_close.setImageResource(R.drawable.icon_select_wifi)
        } else {
            iv_close.setImageResource(R.drawable.icon_unselect)
        }
    }

    inner class ClickProxy {


        fun back() {
            nav().navigateUp()
        }

        fun loadImage() {
            isWifi = !isWifi
            changeWifi()
            requireContext().saveIsWifiLoad(isWifi)
        }

        fun privacy() {
            //requireContext().jumpSystemWebview("http://cloud.cnpowercore.com/terms/chargerCoreConditions.html")
            toPrivacyPolicy(activity!!)
        }

        fun company() {
            requireContext().jumpSystemWebview("https://www.cnchargepoint.com")
        }

        fun language() {
            startActivity<ChangeLanguageActivity>()
        }

        fun clear() {
            clearImageAllCache(requireContext())
            mDrawerViewModel?.cache?.set(getCacheSize(requireContext()))
        }

        fun checkVersion() {

            getAndroidVersion()


        }
        fun logoutClick() {
            logOut()
            sharedViewModel.isLoginSuccess.postValue(false)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1110) {
            if (resultCode != RESULT_OK) {
                AppUpdateManagerFactory.create(requireActivity()).completeUpdate()
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    override fun onResume() {
        super.onResume()
        AppUpdateManagerFactory.create(requireContext())
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    AppUpdateManagerFactory.create(requireContext()).startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        1110
                    );
                }
            }
    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    var size = Math.random().toLong() * 1000
    fun getCacheSize(context: Context): String? {
        try {
//             todo clear

            val disk = CacheDiskStaticUtils.getCacheSize() + CacheDiskUtils.getInstance().cacheSize
            return getFormatSize(
                getFolderSize(
                    File(
                        context.cacheDir
                            .toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
                    )
                )
                        + disk
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                size = if (aFileList.isDirectory) {
                    size + getFolderSize(aFileList)
                } else {
                    size + aFileList.length()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    private fun getFormatSize(size: Long): String? {
        val kiloByte = (size + this.size) / 1024

        if (kiloByte < 1) {
            return size.toString() + "Byte"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte.toDouble()))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte.toDouble()))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte.toDouble()))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString().toString() + "TB"
    }

    /**
     * 清除图片磁盘缓存
     */
    fun clearImageDiskCache(context: Context) {
        try {
            Fresco.getImagePipeline().clearCaches()
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable { Glide.get(context).clearDiskCache() }).start()
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片内存缓存
     */
    fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除图片所有缓存
     */
    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val ImageExternalCatchDir = context.externalCacheDir
            .toString() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
        deleteFolderFile(ImageExternalCatchDir, true)
        size = 0
    }


    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private fun deleteFolderFile(
        filePath: String,
        deleteThisPath: Boolean
    ) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                val file = File(filePath)
                if (file.isDirectory) {
                    val files = file.listFiles()
                    for (file1 in files) {
                        deleteFolderFile(file1.absolutePath, true)
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory) {
                        file.delete()
                    } else {
                        if (file.listFiles().size == 0) {
                            file.delete()
                        }
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getAndroidVersion() {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                 url = Configs.BASE_URL + "oss/staticResource/apk/app-" + data + ".apk"
                if (!data.isNullOrBlank()&&data!!.toInt() > AppUtils.getAppVersionCode()) {
                    if (mayRequestExternalStorage(activity, true)) {
                        customerDownloadUpdates()
                        ToastUtils.showShort("正在下载")
                        version_cl.isEnabled=false
                    }

                }else{
                    ToastUtils.showShort("暂无更新信息")

                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    ToastUtils.showLong(message)
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getAndroidVersion(NameVo(name = "aa"))
            }
        }
    }

    private fun getAndroidInstallPackage() {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {


            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    ToastUtils.showLong(message)
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getAndroidInstallPackage(NameVo(name = "aa"))
            }
        }
    }


    /**
     * 获取应用程序路径
     *
     * @return
     */
    fun getAppPath1(context: Context): String? {
        var path = context.packageName
        path = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.getExternalFilesDir(null)!!.path
            } else {
                Environment.getExternalStorageDirectory().toString() + File.separator + path
            }
        } else {
            Environment.getRootDirectory().toString() + File.separator + path
        }
        return path
    }

    /**
     * 判断申请外部存储所需权限
     *
     * @param context
     * @param isActivate
     * @return
     */
    fun mayRequestExternalStorage(context: Context?, isActivate: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        if (isActivate) {
            ActivityCompat.requestPermissions(
                (context as Activity?)!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            )
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE && grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            customerDownloadUpdates()
            ToastUtils.showShort("正在下载")
            version_cl.isEnabled=false
        }
    }

    /**
     * 自定义下载更新（自己实现逻辑代码、更新提示）
     */
    private fun customerDownloadUpdates() {
        manager!!.checkForUpdates(
            UpgradeOptions.Builder()
                .setIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))
                .setTitle(getString(R.string.app_name))
                .setDescription("下载更新")
                .setUrl(url)
                .setStorage(
                    File(
                        getAppPath1(activity!!) + "/Download/com.upgrade.apk"
                    )
                )
                .setMultithreadPool(1)
                .setMultithreadEnabled(true)
                .setMd5(null)
                .setAutocleanEnabled(true)
                .build(), false
        )
    }
    fun logOut() {
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    ToastUtils.showLong(message)
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