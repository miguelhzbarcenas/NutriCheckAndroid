package com.example.nutricheckproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutricheckproject.R
import com.example.nutricheckproject.data.Patient
import com.example.nutricheckproject.databinding.FragmentCalorieCalculatorBinding
import com.example.nutricheckproject.viewmodel.PatientDetailViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

class CalorieCalculatorFragment : Fragment() {

    private var _binding: FragmentCalorieCalculatorBinding? = null
    private val binding get() = _binding!!

    private val args: CalorieCalculatorFragmentArgs by navArgs()
    private val viewModel: PatientDetailViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    private var currentPatient: Patient? = null
    private var currentTotalCalories: Double = 0.0

    private val formulas = listOf("Mifflin-St Jeor", "Harris-Benedict")

    private val activityFactors = listOf(
        Pair("Sedentario (1.2)", 1.2),
        Pair("Ligero (1.375)", 1.375),
        Pair("Moderado (1.55)", 1.55),
        Pair("Activo (1.725)", 1.725),
        Pair("Muy Activo (1.9)", 1.9)
    )

    private val stressFactors = listOf(
        Pair("Ninguno (1.0)", 1.0),
        Pair("Cirugía Menor (1.2)", 1.2),
        Pair("Fractura (1.2)", 1.2),
        Pair("Infección Leve (1.2)", 1.2),
        Pair("Trauma Esquelético (1.35)", 1.35),
        Pair("Cirugía Mayor (1.4)", 1.4),
        Pair("Sepsis / Quemadura (1.5)", 1.5)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalorieCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdowns()


        try {
            val patientId = UUID.fromString(args.patientId)
            viewModel.loadPatient(patientId)
        } catch (_: Exception) {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.patientState.collectLatest { patient ->
                patient?.let {
                    currentPatient = it
                    calculateCalories()
                }
            }
        }

        binding.btnSaveCalories.setOnClickListener { saveResult() }
    }

    private fun setupDropdowns() {
        val formulaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, formulas)
        binding.actvFormula.setAdapter(formulaAdapter)
        binding.actvFormula.setText(formulas[0], false)
        binding.actvFormula.setOnItemClickListener { _, _, _, _ -> calculateCalories() }

        val activityNames = activityFactors.map { it.first }
        val activityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, activityNames)
        binding.actvActivity.setAdapter(activityAdapter)
        binding.actvActivity.setText(activityNames[0], false) // Default Sedentario
        binding.actvActivity.setOnItemClickListener { _, _, _, _ -> calculateCalories() }

        val stressNames = stressFactors.map { it.first }
        val stressAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, stressNames)
        binding.actvStress.setAdapter(stressAdapter)
        binding.actvStress.setText(stressNames[0], false) // Default Ninguno
        binding.actvStress.setOnItemClickListener { _, _, _, _ -> calculateCalories() }
    }

    private fun calculateCalories() {
        val patient = currentPatient ?: return
        val weight = patient.weight ?: 0.0
        val height = patient.height ?: 0.0
        val age = patient.birthday?.let { getAge(it) } ?: 0
        val isMale = patient.gender == "Masculino"

        if (weight == 0.0 || height == 0.0) return

        var bmr: Double
        val selectedFormula = binding.actvFormula.text.toString()

        if (selectedFormula == formulas[0]) {
            // Mifflin-St Jeor
            val base = (10 * weight) + (6.25 * height) - (5 * age)
            bmr = if (isMale) (base + 5) else (base - 161)
        } else {
            // Harris-Benedict
            bmr = if (isMale) {
                66.5 + (13.75 * weight) + (5.003 * height) - (6.755 * age)
            } else {
                655.1 + (9.563 * weight) + (1.850 * height) - (4.676 * age)
            }
        }

        val activityName = binding.actvActivity.text.toString()
        val activityFactor = activityFactors.find { it.first == activityName }?.second ?: 1.2
        val bmrActivity = bmr * activityFactor

        val stressName = binding.actvStress.text.toString()
        val stressFactor = stressFactors.find { it.first == stressName }?.second ?: 1.0
        val total = bmrActivity * stressFactor

        currentTotalCalories = total
        updateUI(bmr, bmrActivity, total)
    }

    private fun updateUI(bmr: Double, bmrActivity: Double, total: Double) {
        binding.tvBmrValue.text = getString(R.string.kcal, bmr.toInt())
        binding.tvActivityValue.text = getString(R.string.activity_kcal, bmrActivity.toInt())
        binding.tvTotalValue.text = getString(R.string.total_kcal, total.toInt())
    }

    private fun saveResult() {
        val patient = currentPatient ?: return

        val updatedPatient = patient.copy(targetCalories = currentTotalCalories)

        viewModel.savePatient(updatedPatient)
        Toast.makeText(context, getString(R.string.calories_saved), Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun getAge(birthDate: Date): Int {
        val today = Calendar.getInstance()
        val birth = Calendar.getInstance()
        birth.time = birthDate
        var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}