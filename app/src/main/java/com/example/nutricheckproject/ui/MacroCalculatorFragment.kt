package com.example.nutricheckproject.ui

import android.graphics.Color
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
import com.example.nutricheckproject.databinding.FragmentMacroCalculatorBinding
import com.example.nutricheckproject.databinding.ItemMacroCardBinding
import com.example.nutricheckproject.viewmodel.PatientDetailViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import androidx.core.graphics.toColorInt

class MacroCalculatorFragment : Fragment() {

    private var _binding: FragmentMacroCalculatorBinding? = null
    private val binding get() = _binding!!

    private val args: MacroCalculatorFragmentArgs by navArgs()
    private val viewModel: PatientDetailViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    private var targetCalories: Double = 2000.0

    private val carbDivisor = 15.0
    private val protDivisor = 7.0
    private val fatDivisor = 5.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMacroCalculatorBinding.inflate(inflater, container, false)
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
                    targetCalories = if ((it.targetCalories ?: 0.0) > 0) it.targetCalories!! else 2000.0
                    setupUI()
                }
            }
        }
    }

    private fun setupUI() {
        binding.tvTargetCalories.text = getString(R.string.target_kcal, targetCalories.toInt())

        setupCard(binding.cardCarbs, getString(R.string.carbs), getString(R.string.carb_details), "#E3F2FD", "#1976D2")
        setupCard(binding.cardProt, getString(R.string.proteins), getString(R.string.prot_details), "#FFEBEE", "#D32F2F")
        setupCard(binding.cardFat, getString(R.string.fats), getString(R.string.fat_details), "#FFF3E0", "#F57C00")

        val sliderListener = { updateCalculations() }

        binding.sliderCarbs.addOnChangeListener { _, _, _ -> sliderListener() }
        binding.sliderProt.addOnChangeListener { _, _, _ -> sliderListener() }
        binding.sliderFat.addOnChangeListener { _, _, _ -> sliderListener() }

        updateCalculations()
    }

    private fun setupCard(itemBinding: ItemMacroCardBinding, title: String, subtitle: String, bgColor: String, primaryColor: String) {
        itemBinding.root.setCardBackgroundColor(bgColor.toColorInt())
        itemBinding.root.strokeColor = primaryColor.toColorInt()
        itemBinding.tvMacroTitle.text = title
        itemBinding.tvMacroSubtitle.text = subtitle
        itemBinding.tvGramsValue.setTextColor(primaryColor.toColorInt())
    }

    private fun updateCalculations() {
        val carbPct = binding.sliderCarbs.value
        val protPct = binding.sliderProt.value
        val fatPct = binding.sliderFat.value


        binding.tvCarbPercent.text = "${getString(R.string.carbs)}: ${carbPct.toInt()}%"
        binding.tvProtPercent.text = "${getString(R.string.proteins)}: ${protPct.toInt()}%"
        binding.tvFatPercent.text = "${getString(R.string.fats)}: ${fatPct.toInt()}%"

        val total = (carbPct + protPct + fatPct).toInt()
        binding.tvTotalPercent.text = getString(R.string.total_dist, total)
        binding.tvTotalPercent.setTextColor(if (total == 100) "#4CAF50".toColorInt() else Color.RED)

        val carbGrams = (targetCalories * carbPct / 100.0) / 4.0
        val protGrams = (targetCalories * protPct / 100.0) / 4.0
        val fatGrams = (targetCalories * fatPct / 100.0) / 9.0

        val carbEq = carbGrams / carbDivisor
        val protEq = protGrams / protDivisor
        val fatEq = fatGrams / fatDivisor

        updateCardValues(binding.cardCarbs, carbGrams, carbEq, getString(R.string.eq_cereals))
        updateCardValues(binding.cardProt, protGrams, protEq, getString(R.string.eq_legumes))
        updateCardValues(binding.cardFat, fatGrams, fatEq, getString(R.string.eq_oils))
    }

    private fun updateCardValues(itemBinding: ItemMacroCardBinding, grams: Double, equivalents: Double, eqName: String) {
        itemBinding.tvGramsValue.text = getString(R.string.g_format, grams)
        itemBinding.tvEqValue.text = getString(R.string.eq_format, equivalents)
        itemBinding.tvEqName.text = eqName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}