package com.evport.businessapp.ui.page.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.callback.SelectCallback
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.HomeMemberListStringAdapter
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.FileUtils
import com.evport.businessapp.utils.getUser
import com.evport.businessapp.utils.loader.Glide4Engine
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_create_add_family.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

const val ISSUCCESSTOHOME = "ISSUCCESSTOHOME"

class CreateEditFamilyActivity : BaseActivity() {


    private lateinit var mStationViewModel: StationViewModel

    var familyBean =
        FamilyAddBean(homeName = "family", maxPower = "0.0", homePk = "", reservedPower = "0.0")
    var familySettingBean: FamilyBean? = null
    val homePk by lazy {
        intent.getStringExtra(HOME_PK)
    }
    val successToHome by lazy {
        intent.getBooleanExtra(ISSUCCESSTOHOME, false)
    }

    var pkAdd = ""

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_create_add_family, mStationViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    override fun initViewModel() {

        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }


    var memberList = arrayListOf<FamilyMemberBean>()


    var homeMemberListStringAdapter: HomeMemberListStringAdapter? = null
    var reason = ""

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (homePk.isNullOrBlank()) {
            mStationViewModel.title.set("Add Home")
            btn_delete.isVisible = false
            getHomePk()
            val user = this.getUser()
            memberList.add(
                FamilyMemberBean(
                    email = user?.email.toString(),
                    userName = user?.name.toString(),
                    homeMemberPk = user?.userPk.toString(),
                    userPk = user?.userPk.toString(),
                    avatarUrl = user?.avatarUrl
                )
            )
        } else {
            getHomeSetting()
            mStationViewModel.title.set("Home Setting")
        }
        homeMemberListStringAdapter = HomeMemberListStringAdapter(context = baseContext).apply {
            delClick = { item ->
                if (!homePk.isNullOrBlank()) {
                    delMember(item)
                } else {
                    memberList.remove(item)
                    submitList(memberList)
                }
            }
//            setOnItemClickListener { item, position ->
//                if (position == imageList.size - 1 && images.size < 3 && imageList.size < 4) {
//                    requestPermission()
//                }
//            }
        }



        recycler_view_image.apply {
            adapter = homeMemberListStringAdapter
            var layoutManager =
                LinearLayoutManager(this@CreateEditFamilyActivity)
            //设置布局管理器
            recycler_view_image.setLayoutManager(layoutManager)
        }

        homeMemberListStringAdapter?.submitList(memberList)
        checkStatus()
        tv_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkStatus()
            }

        })
        tv_rev.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkStatus()
            }

        })
        tv_max.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkStatus()
            }

        })

    }

    //    新增时使用
    fun getHomePk(isAutoAddHome:Boolean=false) {
        showLoading()
        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data: String?) {
                dismissLoading()
                if (data.isNullOrBlank()) {
                    toast("get pk error,please try again later")
                    finish()
                }
                pkAdd = data.toString()
                familyBean.homePk = pkAdd
                if (isAutoAddHome)
                    addHome()
            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                finish()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.getHomePk()
            }
        }
    }

    //    新增时使用
    fun addHome() {
        showLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(data: Any?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                if (successToHome) {
                    startActivity<UserFamilyActivity>()
                }
                finish()

            }

            override fun onFailure(message: String) {
                dismissLoading()
//                pk过期
                if (message == "error")
                    getHomePk(true)
                else
                    if (!message.isNullOrBlank()){
                        message.toast()
                    }
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.addNewFamily(familyBean)
            }
        }
    }

    //    修改时使用
    fun getHomeSetting() {
        showLoading()
        object : NetworkBoundResource<FamilyBean>(networkStatusCallback = object :
            NetworkStatusCallback<FamilyBean> {

            override fun onSuccess(data: FamilyBean?) {
                dismissLoading()
                familySettingBean = data
                data?.apply {
                    mStationViewModel.name.set(this.homeName)
                    mStationViewModel.maxP.set(this.homeMaxPower)
                    mStationViewModel.reserveP.set(this.homeReservedPower)
                    mStationViewModel.note.set(this.note)
                    mStationViewModel.imgBg.set(this.img)
                    this.members?.let {
                        memberList = it
                        homeMemberListStringAdapter?.submitList(memberList)
                    }

                    homeMemberListStringAdapter?.canDel =
                        this@CreateEditFamilyActivity.getUser()?.userPk == this.homeCreatorUserPk
                }

            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                finish()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<FamilyBean>> {
                return SingletonFactory.apiService.getSettingHome(HomePkBean(homePk))
            }
        }
    }

    fun delHome() {
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
                return SingletonFactory.apiService.delHome(HomePkBean(homePk))
            }
        }
    }

    //    新增&修改验证
    fun emailVef(email: String) {
        showLoading()
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(user: User?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                user?.apply {
                    if (homePk.isNullOrBlank()) {
                        memberList.add(
                            FamilyMemberBean(
                                email = email,
                                userName = user.userName.toString(),
                                homeMemberPk = user.userPk.toString(),
                                userPk = user.userPk.toString(),
                                avatarUrl = user.avatarUrl
                            )
                        )
                        homeMemberListStringAdapter?.submitList(memberList)
                        et_email.setText("")
                        addMemberRL.isVisible = false
                    } else {
                        addMember(et_email.text.toString(), user)

                    }
                }

            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                et_email.setText("")
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.verifyEmail(EmailBean(email))
            }
        }
    }

    fun addMember(email: String, user: User?) {
        showLoading()
        object : NetworkBoundResource<User>(networkStatusCallback = object :
            NetworkStatusCallback<User> {

            override fun onSuccess(data: User?) {
                dismissLoading()
                user?.apply {
                    memberList.add(
                        FamilyMemberBean(
                            email = email,
                            userName = user.userName.toString(),
                            homeMemberPk = user.userPk.toString(),
                            userPk = user.userPk.toString(),
                            avatarUrl = user.avatarUrl
                        )
                    )
                    homeMemberListStringAdapter?.submitList(memberList)
                }

                et_email.setText("")
                addMemberRL.isVisible = false
            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }
                et_email.setText("")
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<User>> {
                return SingletonFactory.apiService.addMember(
                    EmailBean(
                        email = email,
                        homePk = homePk
                    )
                )
            }
        }
    }

    fun delMember(item: FamilyMemberBean) {
        showLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(Any: Any?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                memberList.remove(item)
                homeMemberListStringAdapter?.submitList(memberList)
            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.delMember(EmailBean(homeMemberPk = item.homeMemberPk))
            }
        }
    }

    val powerUpdateBean = PowerUpBean()
    fun powerUpdate() {
        showLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(Any: Any?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                familySettingBean?.apply {
                    this.homeMaxPower = mStationViewModel.maxP.get().toString()
                    this.homeReservedPower = mStationViewModel.reserveP.get().toString()
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
                return SingletonFactory.apiService.powerUpdate(powerUpdateBean)
            }
        }
    }

    val homeUpdateBean = FamilyBean()
    fun homeUpdate() {
        showLoading()
        object : NetworkBoundResource<Any>(networkStatusCallback = object :
            NetworkStatusCallback<Any> {

            override fun onSuccess(Any: Any?) {
                dismissLoading()
                ToastUtils.showShort(getString(R.string.success))
                familySettingBean?.apply {
                    this.note = mStationViewModel.note.get().toString()
                    this.homeName = mStationViewModel.name.get().toString()
                }
                imgB64 = ""
            }

            override fun onFailure(message: String) {
                dismissLoading()
                if (!message.isNullOrBlank()){
                    message.toast()
                }

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<Any>> {
                return SingletonFactory.apiService.homeUpdate(homeUpdateBean)
            }
        }
    }


    fun checkStatus() {
        if (homePk.isNullOrBlank())
            btn_confrim.isEnabled = !tv_password.text.isNullOrBlank()
                    && !tv_max.text.isNullOrBlank()
                    && !tv_rev.text.isNullOrBlank()
        else
            btn_confrim.isEnabled = true

    }

    inner class ClickProxy {
        fun save() {
            if (homePk.isNullOrBlank()) {
                familyBean.homeName = mStationViewModel.name.get().toString()
                familyBean.maxPower =
                    String.format("%.1f", mStationViewModel.maxP.get().toString().toDouble())
                familyBean.reservedPower =
                    String.format("%.1f", mStationViewModel.reserveP.get().toString().toDouble())
                if (familyBean.maxPower
                        .toDouble() < familyBean.reservedPower.toDouble()
                ) {
                    toast("maxPower cannot exceed reservedPower ")
                    return
                }
                familyBean.homePk = pkAdd
                if (!mStationViewModel.note.get().isNullOrBlank())
                    familyBean.note = mStationViewModel.note.get().toString()
                familyBean.members = memberList.map { it.homeMemberPk.toString() }

                addHome()
            } else {
//                修改
                familySettingBean?.apply {
                    var hp = false
                    if (mStationViewModel.name.get().toString() != this.homeName) {
                        homeUpdateBean.homeName = mStationViewModel.name.get().toString()
                        hp = true
                    } else {
                        homeUpdateBean.homeName = null
                    }
                    if (mStationViewModel.note.get().toString() != this.note) {
                        homeUpdateBean.note = mStationViewModel.note.get().toString()
                        hp = true
                    } else {
                        homeUpdateBean.note = null
                    }
                    if (imgB64.isNotBlank()) {
                        hp = true
                        homeUpdateBean.img = imgB64
                    } else {
                        homeUpdateBean.img = null
                    }
                    if (hp) {
                        homeUpdateBean.homePk = this.homePk
                        homeUpdate()
                    }
//
                    var pp = mStationViewModel.maxP.get().toString() != this.homeMaxPower
                            || mStationViewModel.reserveP.get().toString() != this.homeReservedPower

                    powerUpdateBean.maxPower = mStationViewModel.maxP.get().toString()
                    powerUpdateBean.reservedPower = mStationViewModel.reserveP.get().toString()
                    powerUpdateBean.homeSettingPk = this.homeSettingPk
                    if (powerUpdateBean.maxPower.toString()
                            .toDouble() < powerUpdateBean.reservedPower.toString().toDouble()
                    ) {
                        toast("maxPower cannot exceed reservedPower ")
                        return
                    }
                    if (pp) {
                        powerUpdate()
                    }
                    if (!pp && !hp) {
                        toast("Did not modify any items")
                    }
                }

            }
        }

        fun deleteFa() {
            delHome()
        }

        fun back() {
            finish()
        }

        fun addMember() {
            addMemberRL.isVisible = true
            et_email.isFocusable = true
            et_email.isFocusableInTouchMode = true
            et_email.requestFocus()
        }

        fun selectImg() {
            requestPermission()
        }

        fun cancelD() {
            et_email.setText("")
            addMemberRL.isVisible = false
        }

        fun ConfirmD() {
            emailVef(et_email.text.toString())
        }

    }

    /**==============**/

    private fun requestPermission() {
//        val permission: String = Manifest.permission.READ_EXTERNAL_STORAGE
        val permission =
            "${Manifest.permission.READ_EXTERNAL_STORAGE},${Manifest.permission.CAMERA}"

        if (isPermissionGranted(permission)) {
            selectPicture()
        } else {
            setPermission(this, this, permission, 0)
        }
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
                    activity.toast("Should open READ_EXTERNAL_STORAGE permission")
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
//        Matisse.from(this@CreateEditFamilyActivity)
//            .choose(setOf(MimeType.PNG, MimeType.JPEG, MimeType.JPG), true)
//
//            .countable(true)
//            .capture(true)
//            .captureStrategy(
//                CaptureStrategy(true, "com.evport.businessapp.fileprovider", "test")
//            )
//            .maxSelectable(1)
//            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
//            .imageEngine(Glide4Engine())    // for glide-V4
//            .originalEnable(true)
//            .maxOriginalSize(20)
//            .forResult(101)

        EasyPhotos.createAlbum(this, true,false, Glide4Engine())//参数说明：上下文，是否显示相机按
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


    var imgB64 = ""
    private fun handleImageResult(pathList: List<Uri>?) {
        if (pathList?.size!! > 0) {
//            iv_avatar.setImageIsWifi(pathList[0])
//            img_bg.setImageURI(pathList[0])
            Glide.with(this).load(pathList[0]).placeholder(R.drawable.img_family_bg).centerCrop()
                .into(img_bg)

            imgB64 = FileUtils.getBitmapFormUri(this, pathList[0])
            if (imgB64.isBlank()) {
                ToastUtils.showShort("something error , please try again later")
                return
            }
            familyBean.img = imgB64
        } else {
            ToastUtils.showShort(R.string.toast_not_select_image)
        }

    }


}
