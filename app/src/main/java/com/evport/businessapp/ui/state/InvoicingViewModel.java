package com.evport.businessapp.ui.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evport.businessapp.data.bean.InvoicListBean;

import java.util.ArrayList;

public class InvoicingViewModel extends ViewModel {
    public final MutableLiveData<ArrayList<InvoicListBean>> invoiceDatas = new MutableLiveData<>();
    public int allInvoicing = 21;
    public final MutableLiveData<String> count = new MutableLiveData<>("0");
    public final MutableLiveData<String> countMoney = new MutableLiveData<>("0.00");
    public final MutableLiveData<Boolean> isShowMore = new MutableLiveData<>(false);

}
