package com.evport.businessapp.ui.page

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.data.http.networkmanager.NetworkBoundResource
import com.evport.businessapp.data.http.networkmanager.NetworkStatusCallback
import com.evport.businessapp.data.http.networkmanager.Resource
import com.evport.businessapp.data.http.networkmanager.SingletonFactory
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.CompanyAdapter
import com.evport.businessapp.ui.state.InvoicingDetailsViewModel
import com.evport.businessapp.ui.view.IssuingElectronicInvoicepicker
import com.evport.businessapp.utils.ACache
import com.evport.businessapp.utils.LiveBus
import com.lxj.xpopup.XPopup
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_invoicing_details.*
import java.math.BigDecimal
import java.util.regex.Pattern


class InvoicingDetailsFragment : BaseFragment() {

    private var mViewModel: InvoicingDetailsViewModel? = null
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(InvoicingDetailsViewModel::class.java)
    }

    var aCache: ACache? = null
    var companyInfo = CompanyEventBean("", "", "", "")
    var chargingOrderPks = arrayListOf<String>()

    var isSelCompany = false
    private lateinit var adapter: CompanyAdapter
    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = CompanyAdapter(requireContext()).apply {
            selClick = { item, position ->
                true.also { isSelCompany = it }
                corporate_name_et.setText(item!!.companyName)
                corporate_number_et.setText(item!!.companyNumber)
                GONE.also { rv_company.visibility = it }
                closeKeyWord()
            }

        }
        return DataBindingConfig(R.layout.fragment_invoicing_details, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            ).addBindingParam(BR.adapter, adapter)
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
            rv_company.visibility = GONE
        }

        fun enterprise() {
            mViewModel!!.isEnterprise.value = true
            rv_company.visibility = GONE
        }

        fun personal() {
            mViewModel!!.isEnterprise.value = false
            rv_company.visibility = GONE
        }

        fun sendOrder() {
            mViewModel!!.sendchargingOrderDetail.value =
                !mViewModel!!.sendchargingOrderDetail.value!!
            rv_company.visibility = GONE
        }

        fun open() {
            mViewModel!!.isOpen.value = !mViewModel!!.isOpen.value!!
            rv_company.visibility = GONE
        }

        fun more() {
            nav().navigate(R.id.action_global_invoicingMoreFragment, Bundle().apply {
                putString("companyInfo", Gson().toJson(companyInfo))
            })
            rv_company.visibility = GONE
        }

        fun closeCorporateName() {
            corporate_name_et.setText("")
            corporate_number_et.setText("")
            rv_company.visibility = GONE
        }

        fun closeCompanyTaxNo() {
            corporate_number_et.setText("")
            rv_company.visibility = GONE
        }

        fun closeCustomerName() {
            customer_name_et.setText("")
            rv_company.visibility = GONE
        }

        fun closeEmail() {
            email_et.setText("")
            rv_company.visibility = GONE
        }

        fun next() {
            var name = corporate_name_et.text.toString()
            var companyInvoiceNumber = corporate_number_et.text.toString()
            var userName = customer_name_et.text.toString()
            if (mViewModel!!.isEnterprise.value == true) {
                if (name.isNullOrBlank()) {
                    ToastUtils.showShort("请填写公司名称")
                    return
                }
                if (companyInvoiceNumber.isNullOrBlank()) {
                    ToastUtils.showShort("请填写纳税人识别号")
                    return
                }
            } else {
                if (userName.isNullOrBlank()) {
                    ToastUtils.showShort("请输入个人姓名")
                    return
                }
            }
            var email = email_et.text.toString()
            if (email.isNullOrBlank()) {
                ToastUtils.showShort("请输入电子邮箱")
                return
            }
            if (!isEmail(email)) {
                ToastUtils.showShort("请正确输入电子邮箱")
                return
            }
            var companyAddress: String? = ""
            var companyPhone: String? = ""
            var companyBankName: String? = ""
            var companyBankAccount: String? = ""
            if (companyInfo != null) {
                companyAddress = companyInfo!!.companyAddress
                companyPhone = companyInfo!!.companyPhone
                companyBankName = companyInfo!!.companyBankName
                companyBankAccount = companyInfo!!.companyBankAccount
            }

            var createInvoiceVo = CreateInvoiceVo(
                chargingOrderPks = chargingOrderPks,
                invoiceType = if (mViewModel!!.isEnterprise.value == true) 1 else 0,
                name = if (mViewModel!!.isEnterprise.value == true) name else userName,
                email = email,
                sendChargingOrderDetail = mViewModel!!.sendchargingOrderDetail.value,
                isAllChargingOrder = type == InvoicingFragment.SelectStateType.ALL,
                companyInvoiceNumber = if (mViewModel!!.isEnterprise.value == true) companyInvoiceNumber else "",
                companyAddress = companyAddress,
                companyPhone = companyPhone,
                companyBankName = companyBankName,
                companyBankAccount = companyBankAccount,
            )
            XPopup.Builder(requireContext())
                .asCustom(IssuingElectronicInvoicepicker(requireContext()).apply {
                    setCallBack {
                        createInvoice(createInvoiceVo)

                    }
                    setData(createInvoiceVo)
                })
                .show()
        }
    }

    var datas: ArrayList<InvoicListBean>? = null
    val record by lazy {
        arguments?.getString("record")
    }
    val type by lazy {
        arguments?.getSerializable("type")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typeToken = object : TypeToken<ArrayList<InvoicListBean?>?>() {}.type
        datas = Gson().fromJson(record, typeToken)
        aCache = ACache.get(activity)
        initView()
        initObserve()
        initEdit()
        initInfo()


    }

    private fun initInfo() {
        var invoiceVo = aCache!!.getAsString("invoiceVo")
        Log.e("hm----",invoiceVo+"fff")
        if (!invoiceVo.isNullOrBlank()) {
            isSelCompany = true
            var createInvoiceVo =
                Gson().fromJson<CreateInvoiceVo>(invoiceVo, CreateInvoiceVo::class.java)
            mViewModel!!.isEnterprise.value = createInvoiceVo.invoiceType == 1
            mViewModel!!.sendchargingOrderDetail.value = createInvoiceVo.sendChargingOrderDetail
            if (createInvoiceVo.invoiceType == 1) {
                corporate_name_et.setText(createInvoiceVo.name)
                corporate_number_et.setText(createInvoiceVo.companyInvoiceNumber)
            } else {
                customer_name_et.setText(createInvoiceVo.name)
            }
            email_et.setText(createInvoiceVo.email)
            companyInfo = CompanyEventBean(
                companyAddress = createInvoiceVo.companyAddress!!,
                companyBankAccount = createInvoiceVo.companyBankAccount!!,
                companyBankName = createInvoiceVo.companyBankName!!,
                companyPhone = createInvoiceVo.companyPhone!!
            )
            var count = 0;
            if (companyInfo != null) {
                if (!companyInfo!!.companyAddress.isNullOrBlank()) {
                    count++
                }
                if (!companyInfo!!.companyBankAccount.isNullOrBlank()) {
                    count++
                }
                if (!companyInfo!!.companyBankName.isNullOrBlank()) {
                    count++
                }
                if (!companyInfo!!.companyPhone.isNullOrBlank()) {
                    count++
                }
            }
            if (count != 0) {
                mViewModel!!.moreHint.value = "共4项，已填" + count + "项"
            } else {
                mViewModel!!.moreHint.value = "地址、开户行等 (非必填)"
            }
        } else {
           // getCompanyInfoDefult()
        }


    }

    private fun initObserve() {
        LiveBus.getInstance().observeEvent(this, Observer {
            if (it.type == Configs.COMPANY_INFO) {
                companyInfo = it.value as CompanyEventBean
                var count = 0;
                if (companyInfo != null) {
                    if (!companyInfo!!.companyAddress.isNullOrBlank()) {
                        count++
                    }
                    if (!companyInfo!!.companyBankAccount.isNullOrBlank()) {
                        count++
                    }
                    if (!companyInfo!!.companyBankName.isNullOrBlank()) {
                        count++
                    }
                    if (!companyInfo!!.companyPhone.isNullOrBlank()) {
                        count++
                    }
                }
                if (count != 0) {
                    mViewModel!!.moreHint.value = "共4项，已填" + count + "项"
                } else {
                    mViewModel!!.moreHint.value = "地址、开户行等 (非必填)"
                }

            } else if (it.type == Configs.INVOICUNG_REFRESH||it.type==Configs.INVOICUNG_HISTORY) {
                nav().navigateUp()
            }
        })
    }

    fun initView() {

        corporate_name_et.doOnTextChanged { text, start, before, count ->
            /*    if (text!!.isNotEmpty()) {
                    close_corporate_name.visibility = VISIBLE
                } else {
                    close_corporate_name.visibility = INVISIBLE
                }*/
            /*   if (NoFastClickUtils.isFastClick()){

               }*/
            if (!isSelCompany) {
                if (text.toString().isNullOrBlank()){
                    getCompanyInfoDefult()
                }else{
                    getCompanyInfo(text.toString())
                }

                corporate_number_et.text=""
            } else {
                isSelCompany = false
            }

            change()

        }
        corporate_number_et.doOnTextChanged { text, start, before, count ->
            /*   if (text!!.isNotEmpty()) {
                   close_company_tax_no.visibility = VISIBLE
               } else {
                   close_company_tax_no.visibility = INVISIBLE
               }*/
            change()
        }
        customer_name_et.doOnTextChanged { text, start, before, count ->
            /*    if (text!!.isNotEmpty()) {
                    close_customer_name.visibility = VISIBLE
                } else {
                    close_customer_name.visibility = INVISIBLE
                }*/
            change()
        }
        email_et.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                close_email.visibility = VISIBLE
            } else {
                close_email.visibility = INVISIBLE
            }
            change()
        }

        var amounts = BigDecimal("0.0")
        var chargingFees = BigDecimal("0.0")
        var parkingFees = BigDecimal("0.0")
        var serviceFees = BigDecimal("0.0")
        chargingOrderPks.clear()
        datas!!.forEach {
            amounts = amounts.add(it.amount!!)
            chargingFees = chargingFees.add(it.chargingFee!!)
            parkingFees = parkingFees.add(it.parkingFee!!)
            serviceFees = serviceFees.add(it.serviceFee!!)
            chargingOrderPks.add(it.chargingOrderPk!!)
        }

        amount_tv.text = amounts.toString()
        chargingFee.text = chargingFees.toString() + "元"
        parkingFee.text = parkingFees.toString() + "元"
        serviceFee.text = serviceFees.toString() + "元"
    }

    private fun initEdit() {

        corporate_name_et.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_corporate_name.visibility = VISIBLE
                if(corporate_name_et.text.toString().isNullOrBlank()){
                    getCompanyInfoDefult()
                }
            } else {
                close_corporate_name.visibility = INVISIBLE
            }
        }
        corporate_number_et.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_company_tax_no.visibility = VISIBLE
            } else {
                close_company_tax_no.visibility = INVISIBLE
            }
        }
        customer_name_et.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_customer_name.visibility = VISIBLE
            } else {
                close_customer_name.visibility = INVISIBLE
            }
        }
        email_et.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_email.visibility = VISIBLE
            } else {
                close_email.visibility = INVISIBLE
            }
        }

    }

    private fun getCompanyInfo(companyName: String) {
        object : NetworkBoundResource<ArrayList<CompanyBean>>(networkStatusCallback = object :
            NetworkStatusCallback<ArrayList<CompanyBean>> {

            override fun onSuccess(data: ArrayList<CompanyBean>?) {
                dismissLoading()
                mViewModel!!.companyDatas.value = data
                rv_company.visibility = VISIBLE
            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    ToastUtils.showLong(message)
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<CompanyBean>>> {
                return SingletonFactory.apiService.getCompanyInfo(CompanyNameVO(companyName = companyName))
            }
        }
    }

    private fun getCompanyInfoDefult() {
        object : NetworkBoundResource<ArrayList<CompanyBean>>(networkStatusCallback = object :
            NetworkStatusCallback<ArrayList<CompanyBean>> {

            override fun onSuccess(data: ArrayList<CompanyBean>?) {
                dismissLoading()
                if (data!!.isNotEmpty() && data.size != 0) {
                   /* isSelCompany = true
                    corporate_name_et.setText(data[0]!!.companyName)
                    corporate_number_et.setText(data[0]!!.companyNumber)*/
                    mViewModel!!.companyDatas.value = data
                    rv_company.visibility = VISIBLE
                }

            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    ToastUtils.showLong(message)
                }
                dismissLoading()

            }

        }) {
            override fun loadFromNetData(): Observable<Resource<ArrayList<CompanyBean>>> {
                return SingletonFactory.apiService.getCompanyInfoDefult(DefultBean())
            }
        }
    }

    private fun createInvoice(data: CreateInvoiceVo) {

        object : NetworkBoundResource<String>(networkStatusCallback = object :
            NetworkStatusCallback<String> {

            override fun onSuccess(data1: String?) {
                aCache!!.put("invoiceVo", Gson().toJson(data))
                nav().navigate(R.id.action_global_invoicingResultFragment, Bundle().apply
                {
                    putSerializable("type", 0)
                })


            }

            override fun onFailure(message: String) {
                if (!message.isNullOrBlank()) {
                    ToastUtils.showLong(message)
                }
                dismissLoading()
                nav().navigate(R.id.action_global_invoicingResultFragment, Bundle().apply
                {
                    putSerializable("type", 1)
                })
            }

        }) {
            override fun loadFromNetData(): Observable<Resource<String>> {
                return SingletonFactory.apiService.createInvoice(data)
            }
        }
    }

    fun isEmail(email: String?): Boolean {
        if (null == email || "" == email) {
            return false
        }
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*") //复杂匹配
        val m = p.matcher(email)
        return m.matches()
    }

    fun change() {
        if (mViewModel!!.isEnterprise.value!!) {
            next.isEnabled = !corporate_name_et.text.toString()
                .isNullOrBlank() && !corporate_number_et.text.toString()
                .isNullOrBlank() && !email_et.text.toString().isNullOrBlank()
        } else {
            next.isEnabled =
                !customer_name_et.text.toString().isNullOrBlank() && !email_et.text.toString()
                    .isNullOrBlank()
        }
    }
}