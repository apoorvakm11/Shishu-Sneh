package com.ruralhealth.shishusneh.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ruralhealth.shishusneh.databinding.ItemMilestoneBinding
import com.ruralhealth.shishusneh.model.Milestone

class MilestoneAdapter(
    private val onMilestoneCheckChanged: (Milestone, Boolean) -> Unit
) : RecyclerView.Adapter<MilestoneAdapter.MilestoneViewHolder>() {

    private var milestones: List<Milestone> = emptyList()

    fun submitList(list: List<Milestone>) {
        milestones = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MilestoneViewHolder {
        val binding = ItemMilestoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MilestoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MilestoneViewHolder, position: Int) {
        holder.bind(milestones[position])
    }

    override fun getItemCount(): Int = milestones.size

    inner class MilestoneViewHolder(private val binding: ItemMilestoneBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(milestone: Milestone) {
            binding.tvTitle.text = milestone.title
            binding.tvMonth.text = "Expected by ${milestone.monthExpected} Month(s)"
            
            if (milestone.isCompleted) {
                // Removed STRIKE_THRU_TEXT_FLAG as requested
                binding.statusBorder.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                binding.cbCompleted.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_on_background, 0, 0, 0)
            } else {
                binding.statusBorder.setBackgroundColor(Color.parseColor("#D8BFD8")) // Soft Purple
                binding.cbCompleted.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.checkbox_off_background, 0, 0, 0)
            }

            binding.cbCompleted.setOnCheckedChangeListener(null)
            binding.cbCompleted.isChecked = milestone.isCompleted
            
            binding.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onMilestoneCheckChanged(milestone, isChecked)
            }
        }
    }
}
