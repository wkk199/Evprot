package com.evport.businessapp.ui.state;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

public class SettingNewPasswordViewModel extends ViewModel {
    public final ObservableBoolean passwordVisible = new ObservableBoolean();
    public final ObservableBoolean passwordVisible1 = new ObservableBoolean();
    public final ObservableField<String> password = new ObservableField<>();
    public final ObservableField<String> password1 = new ObservableField<>();
}
