package com.evport.businessapp.ui.state;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class BillingHistoryViewMode  extends ViewModel {
    public final ObservableField<Integer> tabPage = new ObservableField<>(1);
}
