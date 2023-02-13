package com.evport.businessapp.ui.state;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;

public class WithdrawalViewModel extends ViewModel {
    public final ObservableBoolean isShowErro = new ObservableBoolean(false);
}
