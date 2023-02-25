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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.Strategy
import com.evport.businessapp.ui.base.PlaceBean
import java.util.*

class PopTimePickerAdapter(var context: Context, var strategy: List<Strategy>) : RecyclerView.Adapter<PopTimePickerAdapter.PlacePredictionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacePredictionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlacePredictionViewHolder(
            inflater.inflate(R.layout.adapter_time_picker, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlacePredictionViewHolder, position: Int) {

        holder.time.text = strategy[position].time
        holder.tv_eleString.text =   "$".plus(strategy[position].energy).plus(" per kWh")
        holder.tv_serviceString.text = "$".plus(strategy[position].service).plus(" per kWh")
        holder.tv_unit.text = "$".plus(strategy[position].park).plus(" per h")

        if (holder.absoluteAdapterPosition == strategy.size-1){
            holder.view_liner.visibility =View.GONE
        }


    }



    override fun getItemCount(): Int {
        return strategy.size
    }

    fun setPredictions(predictions: PlaceBean) {
        notifyDataSetChanged()
    }


    class PlacePredictionViewHolder(itemView: View) : ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.time)
        val tv_eleString: TextView = itemView.findViewById(R.id.tv_eleString)
        val tv_serviceString: TextView = itemView.findViewById(R.id.tv_serviceString)
        val tv_unit: TextView = itemView.findViewById(R.id.tv_unit)
        val view_liner: View = itemView.findViewById(R.id.view_liner)

    }

}