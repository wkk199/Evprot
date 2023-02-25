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
package com.evport.businessapp.ui.page.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.CheckTransaction
import com.evport.businessapp.databinding.AdapterChargeStatusBinding
import com.evport.businessapp.ui.view.PopTimePicker
import com.evport.businessapp.utils.DateUtil
import com.evport.businessapp.utils.socketTypeIsAc
import com.evport.businessapp.utils.toUnit
import com.kunminx.architecture.ui.adapter.SimpleDataBindingAdapter
import com.kunminx.architecture.utils.AdaptScreenUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnConfirmListener
import com.tencent.mm.opensdk.utils.Log
import kotlinx.android.synthetic.main.fragment_changepwd.*
import kotlinx.coroutines.*

/**
 * Create by KunMinX at 20/4/19
 */
class ChargeStatusAdapter(val context: Context?) : SimpleDataBindingAdapter<CheckTransaction, AdapterChargeStatusBinding>(
        context,
        R.layout.adapter_charge_status,
        object : DiffUtil.ItemCallback<CheckTransaction>() {
            override fun areItemsTheSame(
                oldItem: CheckTransaction,
                newItem: CheckTransaction
            ): Boolean {
                return oldItem == newItem
            }

            /**
             * "type" : "Type2",
            "current" : "0.000",
            "meterTimeStamp" : "2020-12-11 09:48:41",
            "settingValue" : null,
            "voltage" : "217.8",
            "usedEnergy" : "0.0",
            "fee" : "5.83",
            "power" : "0.000",
            "transactionPk" : "389806527836061696",
            "chargingSetting" : "QUICKSTART"
             */
            override fun areContentsTheSame(
                oldItem: CheckTransaction,
                newItem: CheckTransaction
            ): Boolean {
                return oldItem == newItem
//                oldItem.type == newItem.type ||
//                        oldItem.current == newItem.current ||
//                        oldItem.meterTimeStamp == newItem.meterTimeStamp ||
//                        oldItem.settingValue == newItem.settingValue ||
//                        oldItem.voltage == newItem.voltage ||
//                        oldItem.usedEnergy == newItem.usedEnergy ||
//                        oldItem.fee == newItem.fee ||
//                        oldItem.power == newItem.power ||
//                        oldItem.transactionPk == newItem.transactionPk ||
//                        oldItem.chargingSetting == newItem.chargingSetting
            }

        }) {
    //    RotateAnimation rotate  = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private var itemChildClick: ItemChildClick? = null

    fun setItemChildClick(itemChildClick: ItemChildClick?) {
        this.itemChildClick = itemChildClick
    }

    var stopClick: ((item: CheckTransaction, p: Int) -> Any)? = null
    var onClickBack: () -> Unit = {}

    fun setOnclick(okBlock: () -> Unit = {}) {
        onClickBack = okBlock
    }

    private fun getDisplayMetrics(context: Context): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics
    }


    var strTYpe = ""

    protected override fun onBindItem(
        binding: AdapterChargeStatusBinding,
        item: CheckTransaction,
        holder: RecyclerView.ViewHolder
    ) { //        动画旋转

        binding.info = item

//        if (currentList.size)
        if (currentList.size ==1){
            binding.imgItemCard.setTopRadiu(0f,0f)
            binding.rlButoon.background= null

            if (strTYpe == "首页") {
                binding.toolbarBack.visibility =View.GONE
                val lp1: RelativeLayout.LayoutParams =
                    binding.llToReply.layoutParams as RelativeLayout.LayoutParams
                lp1.bottomMargin = AdaptScreenUtils.dp2px(context, 62f)
                binding.llToReply.layoutParams = lp1

                val lp : RelativeLayout.LayoutParams = binding.clearFilterLl.layoutParams as RelativeLayout.LayoutParams
                lp.topMargin = AdaptScreenUtils.dp2px(context,108f)
                binding.clearFilterLl.layoutParams = lp

            }else{

                val lp : RelativeLayout.LayoutParams = binding.clearFilterLl.layoutParams as RelativeLayout.LayoutParams
                lp.topMargin = AdaptScreenUtils.dp2px(context,29f)
                binding.clearFilterLl.layoutParams = lp

                binding.toolbarBack.visibility =View.VISIBLE
                val lp1: RelativeLayout.LayoutParams =
                    binding.llToReply.layoutParams as RelativeLayout.LayoutParams
                lp1.bottomMargin = AdaptScreenUtils.dp2px(context, 0f)
                binding.llToReply.layoutParams = lp1
            }

            if (strTYpe == "首页") {
                val lp2: RelativeLayout.LayoutParams =
                    binding.nsView.layoutParams as RelativeLayout.LayoutParams
                lp2.bottomMargin = AdaptScreenUtils.dp2px(context, 165f)
                binding.nsView.layoutParams = lp2
            }else{
                val lp2: RelativeLayout.LayoutParams =
                    binding.nsView.layoutParams as RelativeLayout.LayoutParams
                lp2.bottomMargin = AdaptScreenUtils.dp2px(context, 0f)
                binding.nsView.layoutParams = lp2
            }


            val lp5 : RecyclerView.LayoutParams = binding.rlList.layoutParams as RecyclerView.LayoutParams
            lp5.leftMargin = AdaptScreenUtils.dp2px(context,0f)
            binding.rlList.layoutParams = lp5
            binding.rlList.layoutParams?.width = (getDisplayMetrics(context!!).widthPixels.times(1f)).toInt()


            val lp3 : RelativeLayout.LayoutParams = binding.imgItemCard.layoutParams as RelativeLayout.LayoutParams
            lp3.height = AdaptScreenUtils.dp2px(context,430f)
            binding.imgItemCard.layoutParams = lp3


            val lp4 : RelativeLayout.LayoutParams = binding.llCard2.layoutParams as RelativeLayout.LayoutParams
            lp4.topMargin = AdaptScreenUtils.dp2px(context,358f)
            binding.llCard2.layoutParams = lp4

        }else{
            val lp : RelativeLayout.LayoutParams = binding.clearFilterLl.layoutParams as RelativeLayout.LayoutParams
            lp.topMargin = AdaptScreenUtils.dp2px(context,29f)
            binding.clearFilterLl.layoutParams = lp

            binding.imgItemCard.setTopRadiu(20f,20f)
            binding.rlButoon.background= context!!.resources.getDrawable(R.drawable.shape_yuan_bt1)
            binding.rlList.layoutParams?.width = (getDisplayMetrics(context).widthPixels.times(0.94f)).toInt()
            binding.toolbarBack.visibility =View.GONE
            if (strTYpe == "首页") {
                val lp2: RelativeLayout.LayoutParams =
                    binding.nsView.layoutParams as RelativeLayout.LayoutParams
                lp2.bottomMargin = AdaptScreenUtils.dp2px(context, 155f)
                binding.nsView.layoutParams = lp2
            }else{
                val lp2: RelativeLayout.LayoutParams =
                    binding.nsView.layoutParams as RelativeLayout.LayoutParams
                lp2.bottomMargin = AdaptScreenUtils.dp2px(context, 140f)
                binding.nsView.layoutParams = lp2
            }

            if (strTYpe == "首页") {
                val lp1: RelativeLayout.LayoutParams =
                    binding.llToReply.layoutParams as RelativeLayout.LayoutParams
                lp1.bottomMargin = AdaptScreenUtils.dp2px(context, 62f)
                binding.llToReply.layoutParams = lp1
            }else{
                val lp1: RelativeLayout.LayoutParams =
                    binding.llToReply.layoutParams as RelativeLayout.LayoutParams
                lp1.bottomMargin = AdaptScreenUtils.dp2px(context, 44f)
                binding.llToReply.layoutParams = lp1
            }

            val lp3 : RelativeLayout.LayoutParams = binding.imgItemCard.layoutParams as RelativeLayout.LayoutParams
//            lp3.topMargin = AdaptScreenUtils.dp2px(context,15f)
            lp3.height = AdaptScreenUtils.dp2px(context,335f)
            binding.imgItemCard.layoutParams = lp3

            val lp4 : RelativeLayout.LayoutParams = binding.llCard2.layoutParams as RelativeLayout.LayoutParams
            lp4.topMargin = AdaptScreenUtils.dp2px(context,287f)
            binding.llCard2.layoutParams = lp4

            val lp5 : RecyclerView.LayoutParams = binding.rlList.layoutParams as RecyclerView.LayoutParams
            lp5.rightMargin = AdaptScreenUtils.dp2px(context,5f)
            lp5.leftMargin = AdaptScreenUtils.dp2px(context,6f)
            binding.rlList.layoutParams = lp5

        }



        binding.toolbarBack.setOnClickListener {
            onClickBack.invoke()
        }
//
        if (!item.status.equals("Charging")) {
            binding.btnSetTime.text = mContext.resources.getString(R.string.Waiting)
            binding.btnSetTime.isEnabled = false
        } else {
            binding.btnSetTime.text = mContext.resources.getString(R.string.Stop_charging)
            binding.btnSetTime.isEnabled = true
        }
        binding.btnSetTime.setOnClickListener {

            var popupView =
                XPopup.Builder(context)
                    .asConfirm(
                        mContext.resources.getString(R.string.stop_),
                        mContext.resources.getString(R.string.areyousurrestop_),
                        mContext.resources.getString(R.string.cancel_tv),
                        mContext.resources.getString(R.string.Confirm),
                        OnConfirmListener {
                            stopClick?.invoke(item, holder.absoluteAdapterPosition)
                        },
                        null,
                        false
                    )
            popupView.cancelTextView.setTextColor(0x8193AE)
            popupView.confirmTextView.setTextColor(0x15CD80)

            popupView.show()

        }
        binding.tvLocal.text = item.location
        if (item.chargingSetting?.toLowerCase() == "spendtime") {
            Log.e("hm----startTime", item.startTime.toString())
            Log.e("hm----nowTiem", DateUtil.getNowTime())
            binding.tvChargeTime.setUpTime(item.startTime.toString(), item.settingValue.toString())
        } else {
            android.util.Log.e("TAG", "onBindItem: --------------"+ item.startTime)
            if (item.startTime != null)
                binding.tvChargeTime.setUpTime(item.startTime.toString())
        }

        binding.tvEng.setCompoundDrawablesWithIntrinsicBounds(
            0,
            R.drawable.icon_charging_soc,
            0,
            0
        )
        if (item.type.isNullOrEmpty() || item.type.toString().socketTypeIsAc()) {
            // 交流不能设置百分比
            binding.tvPerscent.isVisible = false
            // binding.ArcProgress.progress = 100
            item.chargingSetting?.apply {
                when {
                    this.toLowerCase() == "soc" -> {
                        binding.tvEng.text = "${item.usedEnergy ?: "--"}KWh"
                    }
                    this.toLowerCase() == "energy" -> {
                        // binding.tvEng.text = "${item.settingValue ?: "--"}度"
                        binding.tvEng.text =
                            "${item.usedEnergy ?: "--"}度/${item.settingValue ?: "--"}KWh"
                    }
                    this.toLowerCase() == "spendtime" -> {
                        binding.tvEng.text = "${item.usedEnergy ?: "--"}KWh"
                    }
                    else -> {
                        binding.tvEng.text = "${item.usedEnergy ?: "--"}KWh"
                    }
                }
            }
        } else {
            binding.tvEng.text = "${item.usedEnergy ?: "--"}KWh"
            binding.tvPerscent.text = "${item.soc ?: "--"}%"
            binding.tvPerscent.isVisible = !item.soc.isNullOrBlank()

            if (item.progress?.toInt() ?: 0 != binding.ArcProgress.progress) {
                binding.ArcProgress.progress = item.progress?.toInt() ?: 0
            }
            item.chargingSetting?.apply {
                when {
                    this.toLowerCase() == "soc" -> {
                        binding.tvPerscent.text =
                            "${item.soc ?: "--"}%/${item.settingValue ?: "--"}%"
                    }
                    this.toLowerCase() == "energy" -> {
                        binding.tvEng.text =
                            "${item.usedEnergy ?: "--"}KWh${item.settingValue ?: "--"}KWh"
                    }
                }
            }


        }
//        val l = ArrayList<Strategy>()
//        l.add(Strategy())
//        item.strategy = l
        item.strategy?.apply {
            if (this.isNotEmpty()) {
                forEach {
                    it.time?.split("-")?.apply {
                        if (DateUtil.containNowDate(this[0].trim(), this[1].trim())) {
                            binding.time.text = it.time
                            binding.tvCfee.text =
                                "$".plus(it.energy).plus("/KWh")
                            binding.tvSfee.text =
                                "$".plus(it.service).plus("/KWh")
                            binding.tvPfee.text =
                                "$".plus(it.park).plus("/h")
                        }
                    }
                }
                binding.time.setOnClickListener {
//                    val s = this.map { it.time }.toTypedArray()
//                    AlertDialog.Builder(mContext)
//                        .setItems(
//                            s
//                        ) { dialogInterface, i ->
//
//                            val i2 = this[i]
//                            binding.time.text = i2.time
//                            binding.tvCfee.text =
//                                "$".plus(i2.energy).plus("/KWh")
//                            binding.tvSfee.text =
//                                "$".plus(i2.service).plus("/KWh")
//                            binding.tvPfee.text =
//                                "$".plus(i2.park).plus("/h")
//                        }.create()
//                        .show()
                    XPopup.Builder(context)
                        .asCustom(PopTimePicker(context!!,this ).apply {
                        })
                        .show()
                }
            } else {
                binding.time.text = "--"
                binding.tvCfee.text =
                    "$".plus("--").plus("/KWh")
                binding.tvSfee.text =
                    "$".plus("--").plus("/KWh")
                binding.tvPfee.text =
                    "$".plus("--").plus("/h")
            }

        }


        /*  GlobalScope.launch(Dispatchers.IO) {
              Log.e("hm----","sdada")
              while(true){
                  for (i in 0..100){
                      delay(100)
                      withContext(Dispatchers.Main) {
                          Log.e("hm----",i.toString())
                          binding.ArcProgress.progress =i
                      }
                  }

              }

          }*/
        var animationDrawable: AnimationDrawable
        binding.powerIv.setBackgroundResource(R.drawable.battery_anim)
        animationDrawable = binding.powerIv.background as AnimationDrawable
        animationDrawable.start()

    }

    interface ItemChildClick {
        fun delete(item: CheckTransaction?)
        fun update(item: CheckTransaction?)
        fun nowStart(item: CheckTransaction?)
        fun remoteStop(item: CheckTransaction?)
        fun autoSwitch(item: CheckTransaction?)
        fun refresh()
    }

    fun stopCharge() {

    }
}