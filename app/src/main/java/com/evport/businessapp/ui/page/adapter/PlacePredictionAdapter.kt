// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.evport.businessapp.ui.page.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.PlaceBean
import java.util.*

/**
 * A [RecyclerView.Adapter] for a [com.google.android.libraries.places.api.model.AutocompletePrediction].
 */
class PlacePredictionAdapter : RecyclerView.Adapter<PlacePredictionAdapter.PlacePredictionViewHolder>() {
    private val predictions: MutableList<PlaceBean> = ArrayList()
    var onPlaceClickListener: ((PlaceBean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlacePredictionViewHolder(
            inflater.inflate(R.layout.place_prediction_item, parent, false))
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val place = predictions[position]
//        holder.setPrediction(place)
        holder.itemView.setOnClickListener {
            onPlaceClickListener?.invoke(place)
        }
        holder.title.text = place.name
        holder.address.text = place.addres

        stringInterceptionChangeRed(holder.title,contentS,place.addres)

    }
    var contentS=""
    override fun getItemCount(): Int {
        return predictions.size
    }

    fun setPredictions(predictions: PlaceBean) {
        this.predictions.addAll(listOf(predictions))
        notifyDataSetChanged()
    }
    fun setPredictions() {
        this.predictions.clear()
        notifyDataSetChanged()
    }

    class PlacePredictionViewHolder(itemView: View) : ViewHolder(itemView) {
         val title: TextView = itemView.findViewById(R.id.text_view_title)
         val address: TextView = itemView.findViewById(R.id.text_view_address)

//        fun setPrediction(prediction: PlaceBean) {
//
//            stringInterceptionChangeRed(   binding.title, contentS, item.title);
//        }
    }
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
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#00A0E9"))
            style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.setText(style)
        }
    }
}