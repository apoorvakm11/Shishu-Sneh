package com.ruralhealth.shishusneh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruralhealth.shishusneh.databinding.FragmentMilestoneBinding
import com.ruralhealth.shishusneh.viewmodel.ShishuViewModel

class MilestoneFragment : Fragment() {
    private var _binding: FragmentMilestoneBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShishuViewModel by activityViewModels()
    private lateinit var adapter: MilestoneAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMilestoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = MilestoneAdapter { milestone, isChecked ->
            val updated = milestone.copy(isCompleted = isChecked)
            viewModel.updateMilestone(updated)
            if (isChecked) {
                playCelebration()
            }
        }
        
        binding.rvMilestones.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMilestones.adapter = adapter

        viewModel.milestones.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            val completed = list.count { it.isCompleted }
            binding.tvProgress.text = "$completed / ${list.size} Completed"
        }
    }

    private fun playCelebration() {
        Toast.makeText(requireContext(), "Yay!! Milestone Achieved! 🎉", Toast.LENGTH_SHORT).show()
        binding.lottieCelebration.visibility = View.VISIBLE
        binding.lottieCelebration.playAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
