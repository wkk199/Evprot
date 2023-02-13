package com.evport.businessapp.ui.page.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.PoiItem
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.evport.businessapp.R
import com.evport.businessapp.databinding.AdapterPoiBinding

class PoiAdapter(context: Context?) : SimpleDataBindingAdapter<PoiItem, AdapterPoiBinding>(
    context,
    R.layout.adapter_poi,
    object : DiffUtil.ItemCallback<PoiItem>() {
        override fun areItemsTheSame(
            oldItem: PoiItem,
            newItem: PoiItem
        ): Boolean {
            return oldItem == newItem
            //                return false;
        }

        override fun areContentsTheSame(
            oldItem: PoiItem,
            newItem: PoiItem
        ): Boolean {
            return oldItem == newItem
        }
    }) {
    protected override fun onBindItem(
        binding: AdapterPoiBinding,
        item: PoiItem,
        holder: RecyclerView.ViewHolder
    ) {
         // binding.info = item
        stringInterceptionChangeRed(   binding.title, contentS, item.title);
        binding. detailedAddress.setText(item.provinceName+item.cityName+item.adName+item.title)

    }

    var contentS=""

    /**
     * CSDN-深海呐
     * TextView部分文字变色
     * keyword = 关键字、需要变色的文字   string = 包含变色文字的全部文字
     */
    fun stringInterceptionChangeRed(textView: TextView, keyword: String?, string: String) {
        if (keyword == null || keyword.trim { it <= ' ' }.isEmpty()) return
        if (!string.contains(keyword)) return
        val start = string.indexOf(keyword)
        val end = start + keyword.length
        if (end != 0 && start != -1) {
            val style = SpannableStringBuilder()
            style.append(string)
            //设置部分文字颜色
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#1569A9"))
            style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.setText(style)
        }
    }
}