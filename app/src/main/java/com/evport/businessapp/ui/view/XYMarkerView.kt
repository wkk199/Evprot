package com.evport.businessapp.ui.view

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.evport.businessapp.R
import com.evport.businessapp.data.bean.StatsData
import java.text.DecimalFormat

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
class XYMarkerView(
    context: Context?
) : MarkerView(context, R.layout.custom_marker_view) {
    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val format: DecimalFormat = DecimalFormat("0.00")

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(
        e: Entry,
        highlight: Highlight
    ) {

//        tvContent.setText(String.format("date:%s, value:%s", xAxisValueFormatter.getFormattedValue(e.getX()), format.format(e.getY())));
        try {
            var text = ""
            val statsData = e.data as StatsData
            statsData.data.forEachIndexed { index, statsDataY ->

                if (index == 0){
                    text = "${statsDataY.currency} ${statsDataY.amount}"
                }else{
                    text += "\n${statsDataY.currency} ${statsDataY.amount}"
//                    text.plus("\n").plus("${statsDataY.currency}-${statsDataY.amount}")
                }

            }
            tvContent.text = text
        } catch (exception: Exception) {
            tvContent.text = ""
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }


}