/*
 * Copyright 2018-2020 KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evport.businessapp.ui.page.adapter;

import android.bluetooth.le.ScanResult;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout;
import com.guanaj.easyswipemenulibrary.State;
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter;
import com.evport.businessapp.R;
import com.evport.businessapp.databinding.AdapterLibraryBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Create by KunMinX at 20/4/19
 */
public class DrawerAdapter extends SimpleDataBindingAdapter<ScanResult, AdapterLibraryBinding> {

    public int menuSelect= -1;
    public ScanResult menuSelectItem;

    public ScanResult getMenuSelectItem() {
        return menuSelectItem;
    }

    public void setMenuSelectItem(ScanResult menuSelectItem) {
        this.menuSelectItem = menuSelectItem;
    }

    public int menuEdit = 2;
    private rightMenuClick  menuClick;

    public int getMenuSelect() {
        return menuSelect;
    }

    public void setMenuSelect(int menuSelect) {
        this.menuSelect = menuSelect;
    }

    public rightMenuClick getMenuClick() {
        return menuClick;
    }

    public void setMenuClick(rightMenuClick menuClick) {
        this.menuClick = menuClick;
    }

    public DrawerAdapter(Context context) {
        super(context, R.layout.adapter_library, new DiffUtil.ItemCallback<ScanResult>() {
            @Override
            public boolean areItemsTheSame(@NonNull ScanResult oldItem, @NonNull ScanResult newItem) {
                return oldItem.getDevice().getName().equals(newItem.getDevice().getName());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ScanResult oldItem, @NonNull ScanResult newItem) {
                return oldItem.getDevice().getName().equals(newItem.getDevice().getName());
            }
        });

        setOnItemClickListener((item, position) -> {

        });
    }

    @Override
    protected void onBindItem(AdapterLibraryBinding binding, ScanResult item, RecyclerView.ViewHolder holder) {
        String name = item.getDevice().getName().substring(6);
        binding.setInfo(name);
        if (menuSelect == holder.getBindingAdapterPosition()){
            binding.ivSelect.setImageResource(R.drawable.icon_save_selected);
        }else {
            binding.ivSelect.setImageResource(R.drawable.icon_uncheck_grey);

        }
//        binding.tvDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (menuClick!=null){
//                    menuClick.rightClick(item,menuDelete);
//                }
//            }
//        });
//        binding.tvEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (menuClick!=null){
//                    menuClick.rightClick(item,menuEdit);
//                }
//            }
//        });
//        binding.content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public interface rightMenuClick{
        void rightClick(ScanResult s, int menu);
    }

    public static void open(EasySwipeMenuLayout layout) {
        Method[] methods = layout.getClass().getDeclaredMethods();
        try {
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getName().equals("handlerSwipeMenu")) {
                    method.setAccessible(true);
                    method.invoke(layout, State.RIGHTOPEN);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

