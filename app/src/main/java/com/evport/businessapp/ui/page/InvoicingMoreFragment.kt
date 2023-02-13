package com.evport.businessapp.ui.page

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.google.gson.Gson
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.CompanyEventBean
import com.evport.businessapp.data.bean.EventBean
import com.evport.businessapp.data.config.Configs
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.state.InvoicingMoreViewModel
import com.evport.businessapp.utils.LiveBus
import kotlinx.android.synthetic.main.fragment_invoicing_more.*

class InvoicingMoreFragment : BaseFragment() {
    private var mViewModel: InvoicingMoreViewModel? = null
    override fun initViewModel() {
        mViewModel = getFragmentViewModel(InvoicingMoreViewModel::class.java)
    }

    val companyInfo by lazy {
        arguments?.getString("companyInfo")
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_invoicing_more, mViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
    }

    inner class ClickProxy {
        fun back() {
            nav().navigateUp()
        }

        fun closeAddress() {
            address_et.setText("")
        }

        fun closePhone() {
            phonee_name_et.setText("")
        }

        fun closeBankOfFeposit() {
            bank_of_deposit_et.setText("")
        }

        fun closeAccountNumber() {
            account_number_et.setText("")
        }

        fun next() {
            var data = CompanyEventBean(
                address_et.text.toString(),
                phonee_name_et.text.toString(),
                bank_of_deposit_et.text.toString(),
                account_number_et.text.toString()
            )
            LiveBus.getInstance().post(EventBean(Configs.COMPANY_INFO, data, ""))
            address_et.setText("")
            phonee_name_et.setText("")
            bank_of_deposit_et.setText("")
            account_number_et.setText("")
            nav().navigateUp()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initEdit()
    }

    fun initView() {

        address_et.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                //  close_address.visibility = View.VISIBLE
                address_number.text = text.length.toString()
                address_number.setTextColor(resources.getColor(R.color.colorTheme))
            } else {
                //  close_address.visibility = View.INVISIBLE
                address_number.text = "0"
                address_number.setTextColor(resources.getColor(R.color.color_8f))
            }
        }

        bank_of_deposit_et.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                //  close_bank_of_deposit.visibility = View.VISIBLE
                bank_of_deposit_number.text = text.length.toString()
                bank_of_deposit_number.setTextColor(resources.getColor(R.color.colorTheme))
            } else {
                //close_bank_of_deposit.visibility = View.INVISIBLE
                bank_of_deposit_number.text = "0"
                bank_of_deposit_number.setTextColor(resources.getColor(R.color.color_8f))
            }
        }
        phonee_name_et.doOnTextChanged { text, start, before, count ->
            /*    if (text!!.isNotEmpty()) {
                    close_phone_name.visibility = View.VISIBLE
                } else {
                    close_phone_name.visibility = View.INVISIBLE
                }*/
        }
        account_number_et.doOnTextChanged { text, start, before, count ->
            /*       if (text!!.isNotEmpty()) {
                       close_account_number.visibility = View.VISIBLE
                   } else {
                       close_account_number.visibility = View.INVISIBLE
                   }*/
        }
        var company = Gson().fromJson<CompanyEventBean>(companyInfo, CompanyEventBean::class.java)
        if (!company.companyAddress.isNullOrBlank()) {
            address_et.setText(company.companyAddress)
        }
        if (!company.companyPhone.isNullOrBlank()) {
            phonee_name_et.setText(company.companyPhone)
        }
        if (!company.companyBankName.isNullOrBlank()) {
            bank_of_deposit_et.setText(company.companyBankName)
        }
        if (!company.companyBankAccount.isNullOrBlank()) {
            account_number_et.setText(company.companyBankAccount)
        }
    }

    private fun initEdit() {
        bank_of_deposit_et.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_bank_of_deposit.visibility = View.VISIBLE
            } else {
                close_bank_of_deposit.visibility = View.INVISIBLE
            }
        }
        phonee_name_et.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_phone_name.visibility = View.VISIBLE
            } else {
                close_phone_name.visibility = View.INVISIBLE
            }
        }
        account_number_et.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_account_number.visibility = View.VISIBLE
            } else {
                close_account_number.visibility = View.INVISIBLE
            }
        }
        address_et.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                close_address.visibility = View.VISIBLE
            } else {
                close_address.visibility = View.INVISIBLE
            }
        }
    }
}