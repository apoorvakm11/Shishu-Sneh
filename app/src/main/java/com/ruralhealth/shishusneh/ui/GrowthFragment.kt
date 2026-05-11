package com.ruralhealth.shishusneh.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.ruralhealth.shishusneh.R
import com.ruralhealth.shishusneh.databinding.FragmentGrowthBinding
import com.ruralhealth.shishusneh.viewmodel.ShishuViewModel

class GrowthFragment : Fragment() {
    private var _binding: FragmentGrowthBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShishuViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGrowthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupChart()

        binding.btnSave.setOnClickListener {
            val monthStr = binding.etMonth.text.toString()
            val weightStr = binding.etWeight.text.toString()
            val heightStr = binding.etHeight.text.toString()

            if (monthStr.isNotEmpty() && weightStr.isNotEmpty() && heightStr.isNotEmpty()) {
                viewModel.addGrowthRecord(
                    monthSeq = monthStr.toIntOrNull() ?: 0,
                    weight = weightStr.toFloatOrNull() ?: 0f,
                    height = heightStr.toFloatOrNull() ?: 0f,
                    dateMillis = System.currentTimeMillis()
                )
                binding.etMonth.text?.clear()
                binding.etWeight.text?.clear()
                binding.etHeight.text?.clear()
                Toast.makeText(requireContext(), "Growth record added!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.growthRecords.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) return@observe

            // Update stats
            val latest = records.lastOrNull()
            latest?.let {
                binding.tvCurrentWeight.text = "${it.weightKg} kg"
            }
            
            val entries = ArrayList<Entry>()
            records.sortedBy { it.monthSequence }.forEach { record ->
                entries.add(Entry(record.monthSequence.toFloat(), record.weightKg))
            }

            val dataSet = LineDataSet(entries, "Weight Progress").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                color = ContextCompat.getColor(requireContext(), R.color.pink_dark)
                valueTextColor = Color.DKGRAY
                lineWidth = 4f
                setDrawCircles(true)
                setCircleColor(ContextCompat.getColor(requireContext(), R.color.pink_dark))
                circleRadius = 6f
                setDrawCircleHole(true)
                circleHoleRadius = 3f
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_gradient)
                fillAlpha = 50
                setDrawValues(false)
            }

            val lineData = LineData(dataSet)
            binding.lineChart.data = lineData
            binding.lineChart.animateY(1000)
            binding.lineChart.invalidate()
        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                axisLineColor = Color.LTGRAY
                textColor = Color.GRAY
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "Mo ${value.toInt()}"
                    }
                }
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#EEEEEE")
                axisLineColor = Color.TRANSPARENT
                textColor = Color.GRAY
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
