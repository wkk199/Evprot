package com.evport.businessapp.ui.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evport.businessapp.data.bean.CompanyBean;

import java.util.ArrayList;

public class InvoicingDetailsViewModel extends ViewModel {
    public final MutableLiveData<ArrayList<CompanyBean>> companyDatas = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isEnterprise = new MutableLiveData<>(true);
    public final MutableLiveData<Boolean> isOpen = new MutableLiveData<>(false);
    public final MutableLiveData<Boolean> sendchargingOrderDetail = new MutableLiveData<>(true);
    public final MutableLiveData<String> moreHint = new MutableLiveData<>("地址、开户行等 (非必填)");
}
