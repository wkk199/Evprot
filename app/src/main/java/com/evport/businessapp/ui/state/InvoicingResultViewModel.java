package com.evport.businessapp.ui.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InvoicingResultViewModel extends ViewModel {
    public final MutableLiveData<Integer> type = new MutableLiveData<>(0);
    public  final MutableLiveData<String> typeHint = new MutableLiveData<>("提交成功");
    public  final MutableLiveData<String> typeHint1 = new MutableLiveData<>("可前往开票历史查看开票进度");
}
