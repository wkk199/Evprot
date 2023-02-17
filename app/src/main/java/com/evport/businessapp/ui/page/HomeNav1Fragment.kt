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
package com.evport.businessapp.ui.page

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.evport.businessapp.BR
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.*
import com.evport.businessapp.ui.base.BaseFragment
import com.evport.businessapp.ui.base.DataBindingConfig
import com.evport.businessapp.ui.page.adapter.NameValueAdapter
import com.evport.businessapp.ui.state.DrawerViewModel
import com.evport.businessapp.ui.state.StatsViewModel
import com.evport.businessapp.ui.view.MyValueFormatter
import com.evport.businessapp.ui.view.PopMonthPicker
import com.evport.businessapp.ui.view.XYMarkerView
import com.evport.businessapp.utils.DateUtil
import com.evport.businessapp.utils.toDayFrendly
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import com.kunminx.architecture.domain.manager.NetState
import com.lxj.xpopup.XPopup
import kotlinx.android.synthetic.main.fragment_home_nav1.*
import java.util.*


/**
 * Create by KunMinX at 19/10/29
 */

class HomeNav1Fragment : BaseFragment(), OnChartValueSelectedListener {
    private var need = true
    private var statsViewModel: StatsViewModel? = null
    private var mDrawerViewModel: DrawerViewModel? = null
    private var points = ArrayList<ChargeSetting>()
    var selectPoints = ArrayList<String>()
    var selectPointsPk = ArrayList<String>()
    var selectPointsB = ArrayList<Boolean>()
    var ek = mutableListOf<StatsData>()

    var selectYear = false
    var selectEnergy = true
    override fun initViewModel() {
        statsViewModel = getFragmentViewModel(StatsViewModel::class.java)
        mDrawerViewModel = getFragmentViewModel(DrawerViewModel::class.java)

    }

    override fun getDataBindingConfig(): DataBindingConfig { //TODO tip: DataBinding 严格模式：
        return DataBindingConfig(R.layout.fragment_home_nav1, statsViewModel)
            .addBindingParam(
                BR.click,
                ClickProxy()
            )
            .addBindingParam(BR.adapter, NameValueAdapter(context))
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        statsViewModel!!.onTabSelectedListener = object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                showLoading()
                when (tab.position) {
//                    0 -> {
//                        statsViewModel!!.dateStr.set("by time of the week")
//                        statsViewModel!!.requestStats.beginTime = getOldDate(-7)
//                        statsViewModel!!.requestStats(statsViewModel!!.requestStats)
//                    }
                    0 -> {
                        selectYear = false
                        val m = DateUtil.longToStringYM(System.currentTimeMillis())
                        statsViewModel?.ChangeTime?.set(m)
                        requestData()
                    }
                    else -> {
                        selectYear = true
                        val m = DateUtil.longToStringY(System.currentTimeMillis())
                        statsViewModel?.ChangeTime?.set(m)
                        requestData()
                    }
                }
            }


            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        }
//        初始化图表
        initChart()

        //
//        获取统计数据
        statsViewModel?.statsLiveData?.observe(
            viewLifecycleOwner,
            Observer { statsData: StatsDataResp ->
                dismissLoading()
                ////                todo data
//                statsData.ek.add(StatsData("2020-02", "12.0"))
//                statsData.ek.add(StatsData("2020-03", "34.001"))
//                statsData.ek.add(StatsData("2020-04", "66"))
//                statsData.ek.add(StatsData("2020-05", "12.111"))
//                statsData.ek.add(StatsData("2020-06", "10"))
//                statsData.ek.add(StatsData("2020-07", "40.456"))
//                statsData.ek.add(StatsData("2020-08", "121"))
//                statsData.ek.add(StatsData("2020-09", "200"))
                statsViewModel?.times?.set(statsData.times)
                ek = statsData.ek ?: arrayListOf()
//                val type =
//                    object : TypeToken<HashMap<String, String>>() {}.type
                val list = ArrayList<NameValue>()
                try {

                    val s = Gson().fromJson(statsData.money, HashMap::class.java)
                    s.forEach {
                        list.add(NameValue(name = it.key.toString(), value = it.value.toString()))
                    }
                    list.sortByDescending { it.value.toFloat() }
                } catch (e: Exception) {

                }
//                list.add(NameValue(name = "CNY",value = "12"))//todo del
                statsViewModel?.listNameValue?.postValue(list)

                statsViewModel?.chargingEnergy?.set(statsData.power.plus("kWh"))
                statsViewModel?.chargingTime?.set(statsData.time.toDayFrendly())
                val fromJson = Gson().fromJson(statsData.money, HashMap::class.java)
                statsViewModel!!.money.set(
                    "$" + fromJson.values.toString()
                        .substring(1, fromJson.values.toString().length - 1)
                )
                if (!ek.isNullOrEmpty())
                    setData(statsData)
                dismissLoading()

            }
        )
        statsViewModel?.ChangeTime?.set(DateUtil.longToStringYM(System.currentTimeMillis()))
        ClickProxy().SelectEnergy()

//          init calendar
        val de = Locale.getDefault()
        var local = Locale.ENGLISH
        var timezone = TimeZone.getDefault()
        when (de.language) {
            Locale.ENGLISH.language, Locale.UK.language, Locale.US.language -> {
                d = 1
                m = 0
                local = Locale.ENGLISH
            }
            "iw" -> {
                d = 0
                m = 1
                local = de
            }
            else -> {
                local = Locale.CHINA
                d = 2
                m = 1
            }

        }
        calendar = Calendar.getInstance(timezone, local)
    }

    override fun onStart() {
        super.onStart()
        requestData()

    }

    override fun onResume() {
        super.onResume()
    }


    override fun onNetworkStateChanged(netState: NetState?) {
        super.onNetworkStateChanged(netState)
        dismissLoading()
    }

    var calendar = Calendar.getInstance()

    //            日
    var d = 2
    var m = 1

    inner class ClickProxy {


        fun ChangeTime() {

            val dialog = DatePickerDialog(
                requireContext(),
                R.style.DatePickerDialog,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = month
                    calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    if (selectYear) {
                        statsViewModel?.ChangeTime?.set(year.toString())
                    } else {
                        statsViewModel?.ChangeTime?.set(DateUtil.longToStringYM(calendar.time.time))
                    }
                    requestData()

                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )

            dialog.datePicker.maxDate = System.currentTimeMillis()
            dialog.datePicker.minDate = DateUtil.stringToLong("2020-01-01 00:00:00")
            ((dialog.datePicker
                .getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(d).visibility =
                View.GONE
            if (selectYear) {
                ((dialog.datePicker
                    .getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(m).visibility =
                    View.GONE
            }
            dialog.show()
        }

        fun SelectEnergy() {
            statsViewModel?.chartValueString?.set("Energy kWh")
            SelectEnergy.setTextColor(resources.getColor(R.color.colorTheme))
            SelectConsumption.setTextColor(resources.getColor(R.color.black_222628))
            SelectEnergy.isSelected = true
            SelectConsumption.isSelected = false
            selectEnergy = true
            requestData()

        }

        fun SelectConsumption() {
            statsViewModel?.chartValueString?.set("Consumption $")
            SelectEnergy.setTextColor(resources.getColor(R.color.black_222628))
            SelectConsumption.setTextColor(resources.getColor(R.color.colorTheme))
            SelectEnergy.isSelected = false
            SelectConsumption.isSelected = true
            selectEnergy = false
            requestData()

        }

        fun SelectCharger() {}
        fun scan() {}
        fun open() {
//            sharedViewModel.openOrCloseDrawer.setValue(true)
        }


        fun selTime() {

            var year = ""
            var month = ""
            if (selectYear) {
                year = statsViewModel!!.ChangeTime.get().toString()
            } else {
                var times = statsViewModel!!.ChangeTime.get().toString().split("-")
                year = times[0]
                month = times[1]
            }
            XPopup.Builder(requireContext())
                .asCustom(PopMonthPicker(requireContext(), selectYear).apply {
                    setCallBack(
                        year,
                        month,
                        okBlock = {
                            Log.e("hm-----selTime", it)
                            var times = it.split("-")
                            if (selectYear) {
                                statsViewModel?.ChangeTime?.set(times[0])
                            } else {
                                statsViewModel?.ChangeTime?.set(it)
                            }
                            requestData()
                        })
                })
                .show()
        }
    }

    var type = "energy"
    var month: String? = null
    var year: String? = null
    var requestStats = RequestStats(
        "energy",
        true,
        null,
        null
    )

    fun requestData(need: Boolean? = true, changeYear: Boolean? = null ?: true) {
        if (changeYear!!) {
            if (selectYear) {
                month = null
                year = statsViewModel?.ChangeTime?.get()
            } else {
                year = null
                month = statsViewModel?.ChangeTime?.get()
            }
        }
        type = if (selectEnergy)
            "energy"
        else
            "money"

        requestStats.type = type
        requestStats.need = need ?: true
        requestStats.month = month
        requestStats.year = year
        showLoading()
        statsViewModel?.requestStats(requestStats)
    }


    fun initChart() {
        val tfLight = Typeface.createFromAsset(resources.assets, "fonts/manrope_medium.ttf")
        val xAxisFormatter: ValueFormatter =
            object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return try {
                        val long = DateUtil.stringToLong(ek[value.toInt()].xValue)
                        if (selectYear) {
                            DateUtil.longToStringY(long)
                        } else {
                            DateUtil.longToStringYM(long)
                        }
                    } catch (e: Exception) {
                        ""
                    }
                }
            }
        val c = chart
//        c.setNoDataText("暂无数据")
        c.setFitBars(true)
        c.setBorderWidth(2f)
        c.xAxis.axisLineColor = resources.getColor(R.color.colorTheme)
        c.xAxis.position = XAxisPosition.BOTTOM
        val description = Description()
        description.text = "."
        chart.description = description

        if (xAxisFormatter != null) {
            c.xAxis.valueFormatter = xAxisFormatter
        }
        //c.xAxis.typeface = tfLight
        c.xAxis.textSize = 10f
        c.xAxis.textColor = resources.getColor(R.color.colorTheme)
        c.xAxis.gridColor = resources.getColor(R.color.white)
        c.xAxis.isGranularityEnabled = true
        // right
        c.axisRight.setDrawLabels(false) // 绘制轴文字
        c.axisRight.setDrawGridLines(false) // 绘制网格线
        c.axisRight.setDrawAxisLine(false) // 绘制轴线
        // left
        c.axisLeft.setDrawLabels(false)
        c.axisLeft.setDrawGridLines(false)
        c.axisLeft.setDrawAxisLine(false)
        c.axisLeft.axisMinimum = 0f
        c.isDragYEnabled = false // 关闭 y轴拖动
        c.isHighlightPerTapEnabled = true
        c.isHighlightFullBarEnabled = true
        c.isDoubleTapToZoomEnabled = false
        c.isHighlightPerDragEnabled = false
        c.isScaleXEnabled = false
        c.isScaleYEnabled = false
        c.legend.isEnabled = false

        chart.setOnChartValueSelectedListener(this)

    }


    /**
     *
     * @param count x
     * @param range y
     */
    private fun setData(stats: StatsDataResp) {


        val ek = stats.ek

        chart.highlightValue(null, false)//隐藏高亮


        if (selectEnergy) {
            var xvalue = ek?.map { it.xValue }

            if (selectYear) {
                xvalue = ek?.map { it.xValue }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xvalue)
            } else {
                xvalue = ek?.map { it.xValue.split("-")[1] }
                var months = getMonths(xvalue as ArrayList<String>)
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
            }
            chart.xAxis.apply {
                textColor = resources.getColor(R.color.color_8f)
            }
            var entries = ArrayList<BarEntry>()
            stats.ek?.forEachIndexed { index, statsData ->
                val barEntry =
                    BarEntry(index.toFloat(), statsData.energy?.toFloat() ?: 0f, statsData)
                entries.add(barEntry)
            }
            val dataSet = BarDataSet(entries, "x")
            if (chart.data != null) {
                chart.data.clearValues()
            }

            dataSet.setGradientColor(
                resources.getColor(R.color.colorTheme_a20),
                resources.getColor(R.color.color_69FFAC)
            )
            dataSet.highLightColor = resources.getColor(R.color.colorTheme)
            val data = BarData(dataSet)
            data.barWidth = 0.4f // 宽度 设置为一半
            data.setValueTextSize(10f)
            data.setValueTextColor(resources.getColor(R.color.colorTheme))
            data.setValueFormatter(MyValueFormatter(""))
            chart.data = data
            chart.marker = null

            chart.setDrawMarkers(false)
            chart.setVisibleXRange(1f, 6f) // 可见视图里最多和最少都显示7


        } else {

            var xvalue = ek?.map { it.xValue }
            if (selectYear) {
                xvalue = ek?.map { it.xValue }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(xvalue)
            } else {
                xvalue = ek?.map { it.xValue.split("-")[1] }
                var months = getMonths(xvalue as ArrayList<String>)
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
            }
            chart.xAxis.apply {
                textColor = resources.getColor(R.color.color_8f)
            }

            var entries = ArrayList<BarEntry>()
            var entries1 = ArrayList<BarEntry>()
            var entries2 = ArrayList<BarEntry>()

            val dataSetList: MutableList<IBarDataSet> = ArrayList()
            stats.ek?.forEachIndexed { index, statsData ->
                if (currentStatsData != null) {
                    if (currentStatsData!!.xValue == statsData.xValue) {
                        val barEntry =
                            BarEntry(index.toFloat(), statsData.amount?.toFloat() ?: 0f, statsData)
                        entries.add(barEntry)
                        val dataset1 = BarDataSet(entries, "x")

                        dataset1.setGradientColor(
                            resources.getColor(R.color.colorTheme),
                            resources.getColor(R.color.colorTheme)
                        )
                        dataset1.highLightColor = resources.getColor(R.color.colorTheme)
                        dataset1.setDrawValues(true)
                        dataSetList.add(index,dataset1)
                    } else {
                        val barEntry = BarEntry(index.toFloat(), statsData.amount?.toFloat() ?: 0f, statsData)
                        entries1.add(barEntry)
                        val dataset2 = BarDataSet(entries1, "x")
                        dataset2.setGradientColor(
                            resources.getColor(R.color.color_72c2f6),
                            resources.getColor(R.color.color_72c2f6)
                        )
                        dataset2.highLightColor = resources.getColor(R.color.colorTheme)
                        dataset2.setDrawValues(true)
                        dataSetList.add(index,dataset2)
                    }

                } else {
                    val barEntry =
                        BarEntry(index.toFloat(), statsData.amount?.toFloat() ?: 0f, statsData)
                    entries2.add(barEntry)
                    val dataset3 = BarDataSet(entries2, "x")
                    //渐变颜色
                    dataset3.setGradientColor(
                        resources.getColor(R.color.color_72c2f6),
                        resources.getColor(R.color.color_72c2f6)
                    )
                    dataset3.highLightColor = resources.getColor(R.color.colorTheme)
                    dataset3.setDrawValues(true)
                    dataSetList.add(index,dataset3)
                }


            }
//            stats.ek?.forEachIndexed { index, statsData ->
//                val barEntry =
//                    BarEntry(index.toFloat(), statsData.amount?.toFloat() ?: 0f, statsData)
//                entries.add(barEntry)
//            }
//            val dataSet = BarDataSet(entries, "x")
//
//            dataSet.setGradientColor(
//                resources.getColor(R.color.colorTheme_a20),
//                resources.getColor(R.color.color_69FFAC)
//            )
//
//            dataSet.highLightColor = resources.getColor(R.color.colorTheme)
//            dataSet.setDrawValues(true)
            val data = BarData(dataSetList)
            data.barWidth = 0.4f // 宽度 设置为一半
            data.setValueTextSize(10f)
            data.setValueTextColor(resources.getColor(R.color.colorTheme))
            data.setValueFormatter(MyValueFormatter(""))
            // data.setValueTextColor(resources.getColor(R.color.white))

            val marker = XYMarkerView(requireContext())
            chart.marker = marker
            chart.setDrawMarkers(false)
            if (chart.data != null) {
                chart.data.clearValues()
            }
            chart.data = data
            chart.setVisibleXRange(1f, 6f) // 可见视图里最多和最少都显示7
        }


    }

    override fun onNothingSelected() {
    }

    var currentStatsData: StatsData? = null
    private val onValueSelectedRectF = RectF()
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        need = false
        currentStatsData = e?.data as StatsData
        Log.e("hm----statsData", Gson().toJson(currentStatsData))
        currentStatsData?.apply {
            if (selectYear) {
                year = xValue
                month = null
            } else {
                month = xValue
                year = null
            }
            statsViewModel!!.ChangeTime.set(xValue)
        }

        requestData()
//        if (e == null) return
//        val bounds: RectF = onValueSelectedRectF
//        chart.getBarBounds(e as BarEntry?, bounds)
//        val position = chart.getPosition(e, AxisDependency.LEFT)
////
//        Log.e("onValueSelected", "onValueSelected")
////        Log.i("position", position.toString())
////
////        Log.i(
////            "x-index",
////            "low: " + chart.lowestVisibleX + ", high: "
////                    + chart.highestVisibleX
////        )
//
//        MPPointF.recycleInstance(position)
    }

    fun getMonths(xvalue: ArrayList<String>): ArrayList<String> {
        var months = arrayListOf<String>()
        var monthsValue = arrayOf(
            getString(R.string.jan),
            getString(R.string.feb),
            getString(R.string.mar),
            getString(R.string.apr),
            getString(R.string.may),
            getString(R.string.jun),
            getString(R.string.jul),
            getString(R.string.aug),
            getString(R.string.sep),
            getString(R.string.oct),
            getString(R.string.nov),
            getString(R.string.dec),
        )
        for (index in 0..xvalue!!.size - 1) {
            val result: Int = xvalue[index].toInt()
            months.add(monthsValue[result - 1])
        }
        return months
    }
}