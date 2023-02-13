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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter;
import com.evport.businessapp.R;
import com.evport.businessapp.data.bean.GunResp;
import com.evport.businessapp.databinding.AdapterChargeGunsBinding;
import com.evport.businessapp.utils.gunStatus;

import java.util.Objects;

/**
 * Create by KunMinX at 20/4/19
 */
public class ChargeGunsAdapter extends SimpleDataBindingAdapter<GunResp, AdapterChargeGunsBinding> {

    private int selectPosition = -1;

    private GunResp selectItem;

    public GunResp getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(GunResp selectItem) {
        this.selectItem = selectItem;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    public ChargeGunsAdapter(Context context) {
        super(context, R.layout.adapter_charge_guns, new DiffUtil.ItemCallback<GunResp>() {
            @Override
            public boolean areItemsTheSame(@NonNull GunResp oldItem, @NonNull GunResp newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull GunResp oldItem, @NonNull GunResp newItem) {
                return oldItem.equals(newItem);
            }
        });

        setOnItemClickListener((item, position) -> {

            if (!gunStatus.Idle.canSelect(item.getStatus())||position<0) {
                ToastUtils.showLong("not available");
                return;
            }
            setSelectPosition(position);
            setSelectItem(item);
            notifyDataSetChanged();
//            可以考虑item.getIconRes()判断
            switch (position){
                case 0:

                    break;
                case 1:
//
                    break;
                default:
//                    退出
                    break;
            }
        });
    }

    @Override
    protected void onBindItem(AdapterChargeGunsBinding binding, GunResp item, RecyclerView.ViewHolder holder) {
        int imgRes =R.drawable.img_single_gun_selected;
        if (holder.getBindingAdapter() != null && holder.getBindingAdapter().getItemCount() > 1) {
            imgRes = R.drawable.img_double_gun;
        }

        if (Objects.equals(item.getStatus(), gunStatus.Charging.name())){
            item.setColorBg(mContext.getResources().getColor(R.color.bg_un_available));
            item.setColorBgStatu(mContext.getResources().getColor(R.color.red));
        }else if (!gunStatus.Charging.canSelect(item.getStatus())){
            item.setColorBg(mContext.getResources().getColor(R.color.offline));
            item.setColorBgStatu(mContext.getResources().getColor(R.color.offline));
            imgRes = R.drawable.icon_gun_offline;
        }else {
            item.setColorBg(mContext.getResources().getColor(R.color.light_green));
            item.setColorBgStatu(mContext.getResources().getColor(R.color.green));
        }

        if (selectPosition == holder.getBindingAdapterPosition()){
            binding.ivImage.setImageResource(imgRes);
            binding.tvId.setTextColor(mContext.getResources().getColor(R.color.white));
            binding.tvText.setTextColor(mContext.getResources().getColor(R.color.white));
//            binding.ivBottom.setVisibility(View.VISIBLE);
//            binding.ivTop.setVisibility(View.VISIBLE);
            item.setItemSelect(true);
        }else {
            binding.ivImage.setImageResource(imgRes);
            binding.tvId.setTextColor(mContext.getResources().getColor(R.color.dark_text_color));
            binding.tvText.setTextColor(mContext.getResources().getColor(R.color.light_text_color));
//            binding.ivBottom.setVisibility(View.GONE);
//            binding.ivTop.setVisibility(View.GONE);
            item.setItemSelect(false);
        }
        binding.setInfo(item);

    }
}
