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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.evport.businessapp.R
import com.evport.businessapp.ui.base.PlaceBean
import java.util.*

/**
 * A [RecyclerView.Adapter] for a [com.google.android.libraries.places.api.model.AutocompletePrediction].
 */
class PlaceHistoryAdapter : RecyclerView.Adapter<PlaceHistoryAdapter.PlacePredictionViewHolder>() {
    private val predictions: MutableList<PlaceBean> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlacePredictionViewHolder(
            inflater.inflate(R.layout.adapter_poi_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {
        val place = predictions[position]
//        holder.setPrediction(place)
        holder.content.setOnClickListener {
            onPlaceClickListener?.invoke(place)
        }

        val index = holder.absoluteAdapterPosition;
        val listSize = predictions.size
        if (index < listSize - 1) {
            holder.cityName.text = place.name
            holder.title.text = place.addres
            holder.content.visibility = View.VISIBLE
            holder.clearHistory.visibility = View.GONE
            holder.view_liner.visibility = View.VISIBLE
        } else {
            holder.content.visibility = View.GONE
            holder.clearHistory.visibility = View.VISIBLE
            holder.view_liner.visibility = View.GONE
        }


        holder.clearHistory.setOnClickListener {
            mOkBlock.invoke()
        }


    }

    private var mOkBlock: () -> Unit = {}

    var onPlaceClickListener: ((PlaceBean) -> Unit)? = null

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
        val content: LinearLayout = itemView.findViewById(R.id.content)
        val cityName: TextView = itemView.findViewById(R.id.cityName)
        val title: TextView = itemView.findViewById(R.id.title)
        val clearHistory: TextView = itemView.findViewById(R.id.clearHistory)
        val view_liner: View = itemView.findViewById(R.id.view_liner)

//        fun setPrediction(prediction: PlaceBean) {
//            title.text = prediction.name
//            address.text = prediction.addres
//        }
    }

    fun setCallBack(okBlock: () -> Unit = {}) {
        mOkBlock = okBlock
    }

    fun setonPlaceClickListener(okBlock: (PlaceBean) -> Unit = {}) {
        onPlaceClickListener = okBlock
    }
}