package com.evport.businessapp.ui.state;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amap.api.services.core.PoiItem;

import java.util.List;

public class SearchMapViewModel extends ViewModel {
    public final MutableLiveData<List<PoiItem>> listOne = new MutableLiveData<>();
    public final MutableLiveData<List<PoiItem>> listHistory = new MutableLiveData<>();
}
