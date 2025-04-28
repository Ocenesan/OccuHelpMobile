package com.example.occuhelp.recap

import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.example.occuhelp.R
import com.example.occuhelp.databinding.ActivityRecapBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class RecapActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRecapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dummy = arrayOf("dummy1", "dummy2", "dummy3", "dummy4", "dummy5")
        val adapterItems = ArrayAdapter<String>(this@RecapActivity, R.layout.list_menu, dummy)
        binding.autoCompleteText.setAdapter(adapterItems)

        //dummy
        val jumlahPerHari = listOf(1200, 2200, 1800, 900, 3000, 2700, 1500)
        val namaHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

        //entry
        val entries = jumlahPerHari.mapIndexed { index, jumlah ->
            BarEntry(index.toFloat(), jumlah.toFloat())
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = ContextCompat.getColor(this@RecapActivity, R.color.primary)
            valueTextColor = Color.BLACK
            valueTextSize = 12f
        }

        val barData = BarData(dataSet)

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)
            setExtraOffsets(4f, 24f, 4f, 8f)
            legend.isEnabled = false
        }

        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(namaHari)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        val yAxis = binding.barChart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${(value / 1000)}k"
            }
        }
        binding.barChart.axisRight.isEnabled = false

        binding.barChart.invalidate()
    }
}