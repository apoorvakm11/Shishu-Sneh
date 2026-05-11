package com.ruralhealth.shishusneh.ui

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ruralhealth.shishusneh.R
import com.ruralhealth.shishusneh.databinding.ItemVaccineBinding
import com.ruralhealth.shishusneh.model.Vaccine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VaccineAdapter(
    private val onVaccineCheckChanged: (Vaccine, Boolean) -> Unit
) : RecyclerView.Adapter<VaccineAdapter.VaccineViewHolder>() {

    private var vaccines: List<Vaccine> = emptyList()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun submitList(list: List<Vaccine>) {
        vaccines = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineViewHolder {
        val binding = ItemVaccineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VaccineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VaccineViewHolder, position: Int) {
        val vaccine = vaccines[position]
        holder.bind(vaccine)
    }

    override fun getItemCount(): Int = vaccines.size

    inner class VaccineViewHolder(private val binding: ItemVaccineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(vaccine: Vaccine) {
            val context = binding.root.context
            binding.tvName.text = vaccine.name
            binding.tvDisease.text = "Prevents: ${vaccine.diseasePrevented}"
            binding.tvDueDate.text = "Due: ${dateFormat.format(Date(vaccine.dueDateMillis))}"
            
            val now = System.currentTimeMillis()
            
            if (vaccine.isGiven) {
                binding.tvName.paintFlags = binding.tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                binding.statusBorder.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                binding.tvStatusBadge.text = "Completed"
                binding.tvStatusBadge.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_green)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_badge_text))
                binding.cbGiven.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_on_background, 0, 0, 0)
            } else {
                binding.tvName.paintFlags = binding.tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                
                if (vaccine.dueDateMillis < now) {
                    // Past date and not given -> DUE
                    binding.statusBorder.setBackgroundColor(Color.parseColor("#F44336")) // Red for overdue
                    binding.tvStatusBadge.text = "Due"
                    binding.tvStatusBadge.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_orange) // Reusing orange bg or could create red
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#D32F2F"))
                } else {
                    // Future date -> UPCOMING
                    binding.statusBorder.setBackgroundColor(Color.parseColor("#FF9800")) // Orange
                    binding.tvStatusBadge.text = "Upcoming"
                    binding.tvStatusBadge.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_orange)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.orange_badge_text))
                }
                binding.cbGiven.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_off_background, 0, 0, 0)
            }
            
            binding.cbGiven.setOnCheckedChangeListener(null)
            binding.cbGiven.isChecked = vaccine.isGiven
            
            binding.cbGiven.setOnCheckedChangeListener { _, isChecked ->
                onVaccineCheckChanged(vaccine, isChecked)
            }
        }
    }
}
