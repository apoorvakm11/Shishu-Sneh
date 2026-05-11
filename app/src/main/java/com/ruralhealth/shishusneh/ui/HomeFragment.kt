package com.ruralhealth.shishusneh.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ruralhealth.shishusneh.R
import com.ruralhealth.shishusneh.databinding.FragmentHomeBinding
import com.ruralhealth.shishusneh.viewmodel.ShishuViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShishuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        startFloatingAnimation()

        viewModel.babyProfile.observe(viewLifecycleOwner) { profile ->
            if (profile != null) {
                binding.tvBabyName.text = profile.name
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val dobStr = sdf.format(Date(profile.dateOfBirthMillis))
                binding.tvBabyAge.text = "Born: $dobStr"
                binding.cardProfile.setOnClickListener(null)
            } else {
                binding.tvBabyName.text = "Setup Profile"
                binding.tvBabyAge.text = "Tap here to add your baby's details"
                binding.cardProfile.setOnClickListener {
                    showCreateProfileDialog()
                }
            }
        }

        binding.fabChat.setOnClickListener {
            findNavController().navigate(R.id.chatFragment)
        }
    }

    private fun startFloatingAnimation() {
        // Gentle floating animation (Up and Down)
        val floatAnimator = ObjectAnimator.ofFloat(binding.ivIllust, "translationY", -20f, 20f)
        floatAnimator.duration = 2500
        floatAnimator.repeatMode = ValueAnimator.REVERSE
        floatAnimator.repeatCount = ValueAnimator.INFINITE
        floatAnimator.interpolator = AccelerateDecelerateInterpolator()
        floatAnimator.start()

        // Subtle breathing scale effect
        val scaleX = ObjectAnimator.ofFloat(binding.ivIllust, "scaleX", 1f, 1.05f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivIllust, "scaleY", 1f, 1.05f)
        listOf(scaleX, scaleY).forEach {
            it.duration = 3000
            it.repeatMode = ValueAnimator.REVERSE
            it.repeatCount = ValueAnimator.INFINITE
            it.start()
        }
    }

    private fun showCreateProfileDialog() {
        val context = requireContext()
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val nameInput = EditText(context).apply {
            hint = "Baby's Name"
        }
        
        val dobText = TextView(context).apply {
            text = "Select Date of Birth"
            setPadding(10, 40, 10, 40)
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black, null))
        }

        val calendar = Calendar.getInstance()
        var selectedDobMillis = calendar.timeInMillis

        dobText.setOnClickListener {
            DatePickerDialog(context, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDobMillis = calendar.timeInMillis
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                dobText.text = "DOB: ${sdf.format(calendar.time)}"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
                datePicker.maxDate = System.currentTimeMillis()
                show()
            }
        }

        layout.addView(nameInput)
        layout.addView(dobText)

        AlertDialog.Builder(context)
            .setTitle("Create Baby's Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                viewModel.saveBabyProfile(if (name.isBlank()) "Baby" else name, selectedDobMillis)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
