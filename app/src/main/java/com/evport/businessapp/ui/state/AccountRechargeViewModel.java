package com.evport.businessapp.ui.state;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;

public class AccountRechargeViewModel extends ViewModel {
    public final ObservableBoolean isWeChat = new ObservableBoolean(true);
}
