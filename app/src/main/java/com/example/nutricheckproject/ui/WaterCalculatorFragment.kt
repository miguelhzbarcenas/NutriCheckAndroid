package com.example.nutricheckproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutricheckproject.R
import com.example.nutricheckproject.databinding.FragmentWaterCalculatorBinding
import com.example.nutricheckproject.viewmodel.PatientDetailViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class WaterCalculatorFragment : Fragment() {

    private var _binding: FragmentWaterCalculatorBinding? = null
    private val binding get() = _binding!!

    private val args: WaterCalculatorFragmentArgs by navArgs()
    private val viewModel: PatientDetailViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    private var patientWeight: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val patientId = UUID.fromString(args.patientId)
            viewModel.loadPatient(patientId)
        } catch (_: Exception) {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.patientState.collectLatest { patient ->
                patient?.let {
                    patientWeight = it.weight ?: 0.0
                    binding.tvPatientWeight.text = getString(R.string.patient_weight_format, patientWeight)
                    calculateWater()
                }
            }
        }

        binding.toggleFactor.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                calculateWater()
            }
        }
    }

    private fun calculateWater() {
        if (patientWeight <= 0) return

        var factor = 35.0 // Default
        var desc = getString(R.string.desc_35)

        when (binding.toggleFactor.checkedButtonId) {
            R.id.btn_30ml -> {
                factor = 30.0
                desc = getString(R.string.desc_30)
            }
            R.id.btn_35ml -> {
                factor = 35.0
                desc = getString(R.string.desc_35)
            }
            R.id.btn_40ml -> {
                factor = 40.0
                desc = getString(R.string.desc_40)
            }
            R.id.btn_45ml -> {
                factor = 45.0
                desc = getString(R.string.desc_45)
            }
        }

        binding.tvFactorDesc.text = desc

        val totalMl = patientWeight * factor
        val totalLiters = totalMl / 1000.0
        val glasses = totalMl / 250.0

        binding.tvResultLiters.text = getString(R.string.liters_format, totalLiters)
        binding.tvResultGlasses.text = getString(R.string.glasses_format, glasses)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}