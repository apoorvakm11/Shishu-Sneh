package com.ruralhealth.shishusneh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruralhealth.shishusneh.databinding.FragmentVaccineBinding
import com.ruralhealth.shishusneh.viewmodel.ShishuViewModel

class VaccineFragment : Fragment() {
    private var _binding: FragmentVaccineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShishuViewModel by activityViewModels()
    private lateinit var adapter: VaccineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVaccineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter = VaccineAdapter { vaccine, isChecked ->
            val updated = vaccine.copy(isGiven = isChecked, givenDateMillis = if(isChecked) System.currentTimeMillis() else null)
            viewModel.updateVaccine(updated)
        }
        
        binding.rvVaccines.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVaccines.adapter = adapter

        viewModel.vaccines.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
