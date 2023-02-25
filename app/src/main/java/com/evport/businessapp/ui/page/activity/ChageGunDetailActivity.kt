package com.evport.businessapp.ui.page.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.evport.businessapp.R
import com.evport.businessapp.BR
import com.evport.businessapp.OnMessageGunDetail
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseActivity
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.StationDeviceFeeAdapter
import com.evport.businessapp.ui.state.ScanViewModel
import com.evport.businessapp.ui.state.StationViewModel
import com.evport.businessapp.utils.*
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.stripe.android.*
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.PaymentMethodsActivityStarter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_chage_gun_detail.*
import kotlinx.android.synthetic.main.fragment_signup1.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class ChageGunDetailActivity : BaseActivity() {

    private lateinit var mStationViewModel: StationViewModel
    private lateinit var mScanViewModel: ScanViewModel
    private var connectorPk = ""
    lateinit var pymentMethod: List<String>

    var cardNumber = ""
    var year = ""
    var month = ""
    var firstName = ""
    var lastName = ""
    var cvc = ""
//    private val scanDate by lazy {
//        intent.getParcelableExtra("scanData") as ConnectorDetail
//    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_chage_gun_detail, mStationViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(
                BR.adapter,
                StationDeviceFeeAdapter(baseContext)
            )
    }

    var setting = ""
    var value: String? = null
    var card = 0
    var cardPk = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this)
            .navigationBarColor(R.color.black)
            .keyboardEnable(false)
            .statusBarDarkFont(true)
            .init()

        initData()
    }

    var isCss = false
    var outTime = 20

    fun initData() {
//        if (connectorPk.isNullOrEmpty())
//            connectorPk = scanDate.connectorPk
        connectorPk = intent.getStringExtra("pk") ?: ""
        getData()
        btn_charge.isEnabled = false
        val s =
            resources.getString(R.string.please_click_here_to_refresh_after_connecting_the_connector)
        tv_tip.text = Html.fromHtml(s)
        tv_tip.setOnClickListener {
            getData()
        }
        tv_tip1.text = Html.fromHtml(s)
        tv_tip1.setOnClickListener {
            getData()
        }
        clear()
        fl_progress.setOnClickListener {
            fl_progress.isVisible = false

        }


        et_energy.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                value = s.toString()
            }

        })


        LiveBus.getInstance().observeEvent(this, androidx.lifecycle.Observer {

            if (it.type == OnMessageGunDetail) {
                dismissLoading()
                h.removeCallbacks(runnableNet)
                try {
                    val gsonObjects =
                        Gson().fromJson<RemoteDataRespBean>(
                            it.value.toString(),
                            RemoteDataRespBean::class.java
                        )
//                            GsonUtils.getGson().fromJson<RemoteResp>(text, RemoteResp::class.java)
                    Log.e("gson:", gsonObjects.toString())
                    gsonObjects?.data?.apply {

                        if ("true" == gsonObjects.success) {

                            if (!percent.isNullOrEmpty()) {
                                fl_progress.visibility = View.VISIBLE
                                fl_progress.removeCallbacks(runnableNet)
                                progress.setDonut_progress(percent)
                                if (percent.toInt() >= 100) {
                                    fl_progress.visibility = View.GONE
                                    ToastUtils.showShort("success")
//                                    getData()
                                    val intent = Intent(
                                        this@ChageGunDetailActivity,
                                        ChargeStatuListActivity::class.java
                                    )
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    ClickProxy().dismissRl()
                                }
                            } else {
                                fl_progress.visibility = View.VISIBLE
                                progress.setDonut_progress("0")
                                showProgress()
                            }
                        } else {
                            ToastUtils.showShort(this.message.toString())
                            fl_progress.visibility = View.GONE
//                            getData()
                        }
                    }
                } catch (e: Exception) {
                    fl_progress.visibility = View.GONE
                    ToastUtils.showShort("something error")
//                    getData()


                }

            } else if (it.type == "onClose") {
                h.removeCallbacks(runnableNet)
            }

        })


        wheel_hour.apply {
            wrapSelectorWheel =true
            refreshByNewDisplayedValues(arrayOfNulls<String>(18).apply {
                for (i in 0..17) {
                    this[i] = if (i < 10) "0${i}" else "${i}"
                }
            })

            setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->

                mFinalHoursMinuteSecond[0] = displayedValues[newVal]
                setFinalHoursMinuteSecond()
            }

//            value = displayedValues.indexOfFirst { it.toInt() == mCurrHours }
        }

        wheel_minute.apply {
            wrapSelectorWheel =true
            refreshByNewDisplayedValues(arrayOfNulls<String>(60).apply {
                for (i in 0..59) {
                    this[i] = if (i < 10) "0${i}" else "${i}"
                }
            })
            setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                mFinalHoursMinuteSecond[1] = displayedValues[newVal]
                setFinalHoursMinuteSecond()
            }

//            value = displayedValues.indexOfFirst { it.toInt() == mCurrMinute }
        }
        wheel_second.apply {
            wrapSelectorWheel =true
            refreshByNewDisplayedValues(arrayOfNulls<String>(60).apply {
                for (i in 0..59) {
                    this[i] = if (i < 10) "0${i}" else "${i}"
                }
            })
            setOnValueChangeListenerInScrolling { picker, oldVal, newVal ->
                mFinalHoursMinuteSecond[2] = displayedValues[newVal]
                setFinalHoursMinuteSecond()
            }

//            value = displayedValues.indexOfFirst { it.toInt() == mCurrSecond }
        }




        et_energy.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                if (s.toString().isEmpty()) {


                    btn_to_charge.isEnabled = false
                    return
                }
                if (s.toString().toInt() <= 0) {
                    "Not less than one kwh".toast()
                    btn_to_charge.isEnabled = false
                    return
                }
                checkBtn()

            }

        })


    }


    fun setFinalHoursMinuteSecond() {


        if (mFinalHoursMinuteSecond[0] == null) mFinalHoursMinuteSecond[0] =
            wheel_hour.value.toString()
        if (mFinalHoursMinuteSecond[1] == null) mFinalHoursMinuteSecond[1] =
            wheel_minute.value.toString()
        if (mFinalHoursMinuteSecond[2] == null) mFinalHoursMinuteSecond[2] =
            wheel_second.value.toString()

        value =
            ((mFinalHoursMinuteSecond[0]!!.toInt() * 60 * 60 + mFinalHoursMinuteSecond[1]!!.toInt() * 60 + mFinalHoursMinuteSecond[2]!!.toInt()) * 1000).toString()

        checkBtn()


    }

    private var mCurrHours: Int = DateUtil.getCurrDate("HH").toInt()
    private var mCurrMinute: Int = DateUtil.getCurrDate("mm").toInt()
    private var mCurrSecond: Int = DateUtil.getCurrDate("ss").toInt()
    private var mFinalHoursMinuteSecond: Array<String?> = arrayOfNulls(3)
    fun showProgress() {
        fl_progress.isVisible = true
        outTime = if (isCss) {
            45
        } else {
            20
        }
//        fl_progress.postDelayed(runnableNet, outTime * 1000L)
//        upProgress()
    }

    override fun onResume() {
        super.onResume()
        fl_progress.isVisible = false
    }

    override fun initViewModel() {
        mScanViewModel = getActivityViewModel(ScanViewModel::class.java)
        mStationViewModel = getActivityViewModel(StationViewModel::class.java)
    }

    fun getData() {
        showLoading()
        object : NetworkBoundResource<ConnectorDetail>(networkStatusCallback = object :
            NetworkStatusCallback<ConnectorDetail> {

            override fun onSuccess(data: ConnectorDetail?) {
                dismissLoading()
                if (data != null) {
                    Log.e("hm---data", Gson().toJson(data))
                    mStationViewModel.info.value = data
                    mStationViewModel.Fee.value = data.fee
                    data.socket.toString().socketTypeIcon()?.apply {
                        img_socket.setImageResource(this)
                    }
                    pymentMethod = data.paymentMethod!!;
                    if (data.status == "Offline" || data.status == "Idle" || data.status == "Charging" || data.status == "Unavailable" || data.status == "Faulted") {
                        btn_charge.isEnabled = false
                    } else {
                        btn_charge.isEnabled = true
                    }

                    /*    tv_card_credit.isVisible = !data.pk.isNullOrEmpty()
                        Credit_card.isVisible = !data.pk.isNullOrEmpty()*/
                    if (getType() == 0) {
                        tv_card_credit.isVisible = false
                        Credit_card.isVisible = false
                    } else {
                        tv_card_credit.isVisible = true
                        Credit_card.isVisible = true
                    }

                    tv_sco.isVisible = !data.socket.toString().socketTypeIsAc()
                    data.pk?.let {
                        cardPk = it

                    }

                    Log.e("TAG", "onSuccess:------------------- " + cardPk)

                    data.socket.toString().socketTypeIcon()?.apply {
                        iv_image.setImageResource(this)
                    }

                    //直流
                    isCss = data.socket != null && (data.socket == "CCS1" || data.socket == "CCS2" || data.socket == "CHAdeMO")


                    if (isCss){
                        line7.visibility =View.VISIBLE
                        tv_sco.visibility = View.VISIBLE
                    }else{
                        line7.visibility =View.GONE
                        tv_sco.visibility = View.GONE

                    }

//                    showProgress()
                }

            }

            override fun onFailure(message: String) {
                message.toast()
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ConnectorDetail>> {
                return SingletonFactory.apiService.connectorDetail(Connector(connectorPk = connectorPk))
            }
        }
    }


    inner class ClickProxy {

        fun back() {
            finish()
        }

        fun howUse() {
            startActivity<HowUseActivity>()
        }

        fun send() {
            rl_toCharge.isVisible = true
        }

        fun toCharge() {

            if (setting == "spendTime") {
                if (value!!.toInt() < 60000) {
                    "The charging time cannot be less than one minute".toast()
                    return
                }
            }
            if (setting == "energy") {
                if (value!!.toInt() <= 0) {
                    "Not less than one kwh".toast()
                    return
                }
            }


            if (card == 1) {
                remoteStart()
            } else if (card == 2) {

                if (getType() == 1) {
                    remoteStart()
                } else {
                    if (TextUtils.isEmpty(setting)) {
                        toast("Please select a Charging settings")
                        return
                    }
                    Log.e("hm---paymentMethod", "2")
                    this@ChageGunDetailActivity.data = RequestChargeChange(
                        paymentMethod = "Cybersource",
                        card = cardNumber,
                        year = year,
                        month = month,
                        firstName = firstName,
                        lastName = lastName,
                        setting = setting,
                        value = value,
                        connectorPk = connectorPk,
                        cvc = cvc
                    )
                    remoteStart()
                }
            }

        }

        fun toChargeLL() {
        }

        fun dismissRl() {
            rl_toCharge.isVisible = false
            clear()
        }

        fun quick() {
            selectQuick()
        }

        fun time() {
            selectTime()
        }

        fun energy() {
            selectEnergy()
        }

        fun soc() {
            selectSOC()
        }

        fun timeOpen() {

//            val now = Calendar.getInstance()
            val mTimePicker = MyTimePickerDialog(
                this@ChageGunDetailActivity,
                MyTimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, seconds ->
                    val s = "${String.format("%1$02d", hourOfDay)}:${
                        String.format(
                            "%1$02d",
                            minute
                        )
                    }:${String.format("%1$02d", seconds)}"

                    tv_set_time.text = s
//                    val long =
                    value = ((hourOfDay * 60 * 60 + minute * 60 + seconds) * 1000).toString()
                },
                0,
                0,
                0,
                true
            )
            mTimePicker.show()
        }

        fun chargeCard() {
            selectCardCharge()
        }

        fun creditCard() {
            selectCardCredit()
        }


    }

    fun clear() {
        tv_quick.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_energy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_card_charge.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.icon_balance_tag,
            0,
            R.drawable.icon_circle_uncheck,
            0
        )
        tv_card_credit.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.icon_card_tag,
            0,
            R.drawable.icon_circle_uncheck,
            0
        )
        fl_time.isVisible = false
        line11.isVisible = false
        ll_energy.isVisible = false
        et_energy.setText("")
        tv_set_time.text = ""
        card = 0
        setting = ""
        value = null
        checkBtn()
    }

    fun selectQuick() {
        tv_quick.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.icon_circle_check_radio,
            0
        )
        tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_energy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_sco.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        ll_soc.isVisible = false
        fl_time.isVisible = false
        line11.isVisible = false
        ll_energy.isVisible = false
        et_energy.setText("")
        tv_set_time.text = ""
        setting = "quickStart"
        value = null
        checkBtn()
    }

    fun selectTime() {
        tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_check_radio, 0)
        tv_quick.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_energy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_sco.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        ll_soc.isVisible = false
//        ll_time.isVisible = true
        fl_time.isVisible = true
        line11.isVisible = false
        ll_energy.isVisible = false
        et_energy.setText("")
        setting = "spendTime"
        value = tv_set_time.text.toString()

        setFinalHoursMinuteSecond()

    }

    fun selectEnergy() {
        tv_energy.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.icon_circle_check_radio,
            0
        )
        tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_quick.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_sco.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        ll_soc.isVisible = false
//        ll_time.isVisible = false
        fl_time.isVisible = false
        line11.isVisible = true
        ll_energy.isVisible = true
        tv_set_time.text = ""
        setting = "energy"
        value = et_energy.text.toString()
    }

    fun selectSOC() {
        tv_sco.setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            R.drawable.icon_circle_check_radio,
            0
        )
        tv_energy.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_time.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
        tv_quick.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_circle_uncheck, 0)
//        ll_time.isVisible = false
        ll_energy.isVisible = false
        ll_soc.isVisible = true
        fl_time.isVisible = false
        line11.isVisible = false
        tv_set_time.text = ""
        setting = "SoC"
        bubbleSeekBar.setProgress(2.5f)
        bubbleSeekBar.setIndicatorText("0")
        bubbleSeekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                bubbleSeekBar.setIndicatorText((leftValue - 2.5).toString().split(".")[0])
                this@ChageGunDetailActivity.value = (leftValue - 2.5).toString().split(".")[0]
                if (this@ChageGunDetailActivity.value!!.toInt() < 0 || this@ChageGunDetailActivity.value.equals(
                        "-0"
                    )
                ) {
                    bubbleSeekBar.setProgress(2.5f)
                    bubbleSeekBar.setIndicatorText("0")
                    this@ChageGunDetailActivity.value = "0"
                }
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }
        })
    }

    fun selectCardCharge() {
        tv_card_charge.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.icon_balance_tag,
            0,
            R.drawable.icon_circle_check_radio,
            0
        )
        tv_card_credit.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.icon_card_tag,
            0,
            R.drawable.icon_circle_uncheck,
            0
        )
        methodId = ""
        card = 1
        checkBtn()
    }

    fun getType(): Int {
        if (pymentMethod == null || pymentMethod.size == 0) {
            return 0
        }
        if (pymentMethod.size == 2) {
            return 1
        } else {
            if (pymentMethod.get(0).equals("Stripe")) {
                return 1
            } else {
                return 2
            }
        }

    }

    fun selectCardCredit() {
        tv_card_charge.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.icon_balance_tag,
            0,
            R.drawable.icon_circle_uncheck,
            0
        )
        tv_card_credit.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.icon_card_tag,
            0,
            R.drawable.icon_circle_uncheck,
            0
        )
        card = 2
//        val config = PaymentConfiguration
//        val s = PaymentMethodsActivity()
        if (getType() == 1) {
            PaymentConfiguration.init(
                applicationContext,
                cardPk
            )
            CustomerSession.initCustomerSession(
                applicationContext,
                ExampleEphemeralKeyProvider(connectorPk)
            )
            val a = PaymentMethodsActivityStarter.Args.Builder().build()
            PaymentMethodsActivityStarter(this).startForResult(a)
        } else if (getType() == 2) {
            val intent = Intent(this, CreatCardActivity::class.java)
            startActivityForResult(intent, 100)
        }

        checkBtn()
    }

    var methodId = ""
    var intentId: String? = null

    fun checkBtn() {
        if (setting == "spendTime") {
            if (value!!.toInt() <= 0) {
                btn_to_charge.isEnabled = false
                return
            }
        }

        when (card) {
            1 -> {
                Log.e("hm---paymentMethod", "3")
                data =
                    RequestChargeChange(
                        setting = setting,
                        value = value,
                        connectorPk = connectorPk,
                        methodId = "",
                        intentId = intentId
                    )
                when (setting) {
                    "spendTime", "energy" -> {
                        btn_to_charge.isEnabled = !value.isNullOrEmpty()
                    }
                    "quickStart", "SoC" -> {
                        btn_to_charge.isEnabled = true
                    }
                    else -> btn_to_charge.isEnabled = false
                }

            }
            2 -> {
                Log.e("hm---paymentMethod", "4")
                data =
                    RequestChargeChange(
                        setting = setting,
                        value = value,
                        connectorPk = connectorPk,
                        methodId = methodId,
                        intentId = intentId,
                        paymentMethod = "stripe"
                    )
                if (methodId.isNotEmpty()) {
                    when (setting) {
                        "spendTime", "energy" -> {
                            btn_to_charge.isEnabled = !value.isNullOrEmpty()
                        }
                        "quickStart", "SoC" -> {
                            btn_to_charge.isEnabled = true
                        }
                        else -> btn_to_charge.isEnabled = false
                    }
                } else {
                    btn_to_charge.isEnabled = true
                }
            }
            else -> btn_to_charge.isEnabled = false

        }

    }

    var data =
        RequestChargeChange(
            setting = setting,
            value = value,
            connectorPk = connectorPk,
            methodId = methodId,
            intentId = intentId,
        )

    fun remoteStart() {
        showLoading()
        object : NetworkBoundResource<RemoteStartRep>(networkStatusCallback = object :
            NetworkStatusCallback<RemoteStartRep> {

            override fun onSuccess(data: RemoteStartRep?) {
                dismissLoading()
                data?.apply {
                    if (status == "Success") {
                        Log.e("hm---data", Gson().toJson(data))
                        showLoading()
//                        Handler().postDelayed({
//                            startActivity<ChargeStatuListActivity>()
//                        },3000)
                    } else if (status == "requires_action") {
//                            3d验证
//                        val uiCustomization =
//                            PaymentAuthConfig.Stripe3ds2Config
//                            .Builder()
//                            .build()
//
                        PaymentAuthConfig.init(
                            PaymentAuthConfig.Builder()
                                .set3ds2Config(
                                    PaymentAuthConfig.Stripe3ds2Config.Builder()
                                        .setTimeout(5)
                                        .build()
                                )
                                .build()
                        )

                        stripe = Stripe(
                            baseContext,
                            cardPk
                        )
                        this@ChageGunDetailActivity.intentId = intentId
                        stripe?.handleNextActionForPayment(
                            this@ChageGunDetailActivity,
                            clientSecret!!
                        )


                    }
                }

            }

            override fun onFailure(message: String) {

                dismissLoading()
                tv_card_credit.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_circle_uncheck,
                    0
                )
//                NetworkStateManager.getInstance()
//                    .networkStateCallback.postValue(NetState("remoteStart"))
                Log.e("hm----onFailure", message);
                message.toast()
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<RemoteStartRep>> {
                Log.e("hm---data", Gson().toJson(data))
                return SingletonFactory.apiService.remoteStartPc(data)
            }
        }

    }

    var stripe: Stripe? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (getType() == 1) {
            if (requestCode == PaymentMethodsActivityStarter.REQUEST_CODE && data != null) {
                val result = PaymentMethodsActivityStarter.Result.fromIntent(data)
                val paymentMethod = result?.paymentMethod
                methodId = paymentMethod?.id ?: ""
                tv_card_credit.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_card_tag,
                    0,
                    R.drawable.icon_circle_check_radio,
                    0
                )
                checkBtn()
                // use paymentMethod
            }
            stripe?.onPaymentResult(
                requestCode,
                data,
                object : ApiResultCallback<PaymentIntentResult> {
                    override fun onSuccess(result: PaymentIntentResult) {
                        stopLoading()
                        if (result.intent.status == StripeIntent.Status.Succeeded) {
                            Log.e("hm---paymentMethod", "1")
                            this@ChageGunDetailActivity.data = RequestChargeChange(
                                setting = setting,
                                value = value,
                                connectorPk = connectorPk,
                                methodId = methodId,
                                intentId = intentId,
                                paymentMethod = "stripe"
                            )
                            remoteStart()
                        } else {
                            ToastUtils.showShort("something went wrong, please try again later")
                        }

                    }

                    override fun onError(e: Exception) {
                        stopLoading()
                        ToastUtils.showShort(e.message.toString())
                    }
                }
            )
        } else if (getType() == 2) {
            if (requestCode == 100 && data != null) {
                tv_card_credit.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_circle_check_radio,
                    0
                )
                checkBtn()

                cardNumber = data.getStringExtra("cardNumber").toString()
                year = data.getStringExtra("year").toString()
                month = data.getStringExtra("month").toString()
                firstName = data.getStringExtra("firstName").toString()
                lastName = data.getStringExtra("lastName").toString()
                cvc = data.getStringExtra("cvc").toString()


                // remoteStart()

            }

        }

    }


    val h = Handler()
    var runnableNet = Runnable {
        dismissLoading()
        //
        AlertDialog
            .Builder(this)
            .setTitle("Operation timed out")
            .setMessage("The operation may be successful, please click confirm to return to the previous page")
            .setPositiveButton("confirm") { p1, p2 ->
                finish()
            }
            .create().show()
    }

    fun upProgress() {

        if (time < outTime + time) {
            progress.setDonut_progress(time.toString())
            time++
        }
        progress.postDelayed({ upProgress() }, 1000)
    }

    var time = 30


}