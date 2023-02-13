package com.evport.businessapp.ui.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.evport.businessapp.data.bean.InvoiceHistoryBean;

import java.util.ArrayList;

public class InvoicingSucceededViewModel extends ViewModel {
    public final MutableLiveData<ArrayList<InvoiceHistoryBean>> invoiceDatas = new MutableLiveData<>();
}
