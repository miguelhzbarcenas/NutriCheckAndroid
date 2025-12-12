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
import com.example.nutricheckproject.databinding.FragmentEspenCalculatorBinding
import com.example.nutricheckproject.viewmodel.PatientDetailViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class EspenCalculatorFragment : Fragment() {

    private var _binding: FragmentEspenCalculatorBinding? = null
    private val binding get() = _binding!!

    private val args: EspenCalculatorFragmentArgs by navArgs()
    private val viewModel: PatientDetailViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEspenCalculatorBinding.inflate(inflater, container, false)
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
                patient?.let { calculateEspen(it.weight ?: 0.0, it.height ?: 0.0) }
            }
        }
    }

    private fun calculateEspen(weightKg: Double, heightCm: Double) {
        if (weightKg <= 0 || heightCm <= 0) {
            binding.tvResultValue.text = getString(R.string.insufficient_data)
            return
        }

        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)

        binding.tvBmiValue.text = String.format("%.1f kg/m²", bmi)

        var minKcal: Double
        var maxKcal: Double
        var categoryText: String
        var formulaText: String

        if (bmi < 30) {

            categoryText = getString(R.string.cat_normal)
            formulaText = getString(R.string.form_normal)
            minKcal = 25 * weightKg
            maxKcal = 30 * weightKg
        } else if (bmi >= 30 && bmi <= 50) {

            categoryText = getString(R.string.cat_obesity_1_2)
            formulaText = getString(R.string.form_obesity_1_2)
            minKcal = 11 * weightKg
            maxKcal = 14 * weightKg
        } else {

            categoryText = getString(R.string.cat_obesity_3)


            val idealWeight = 22.5 * (heightM * heightM)

            formulaText = getString(R.string.form_obesity_3, idealWeight.toInt())
            minKcal = 22 * idealWeight
            maxKcal = 25 * idealWeight
        }

        binding.tvCategoryValue.text = categoryText
        binding.tvFormulaDesc.text = formulaText
        binding.tvResultValue.text = getString(R.string.kcal_day_range, minKcal.toInt(), maxKcal.toInt())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}