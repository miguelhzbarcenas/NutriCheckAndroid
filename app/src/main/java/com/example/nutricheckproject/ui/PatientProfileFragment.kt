package com.example.nutricheckproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutricheckproject.R
import com.example.nutricheckproject.data.Patient
import com.example.nutricheckproject.databinding.FragmentPatientProfileBinding
import com.example.nutricheckproject.viewmodel.PatientDetailViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class PatientProfileFragment : Fragment() {

    private var _binding: FragmentPatientProfileBinding? = null
    private val binding get() = _binding!!

    private val args: PatientProfileFragmentArgs by navArgs()
    private val viewModel: PatientDetailViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val patientId = UUID.fromString(args.patientId)
            viewModel.loadPatient(patientId)
        } catch (_: IllegalArgumentException) {
            Toast.makeText(context, R.string.error_id_de_paciente_inv_lido, Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {

        binding.btnEditPatient.setOnClickListener {
            val action = PatientProfileFragmentDirections.actionPatientProfileToPatientDetail(args.patientId)
            findNavController().navigate(action)
        }

        binding.btnToolCalories.setOnClickListener {
            val action = PatientProfileFragmentDirections.actionPatientProfileToCalorieCalculator(args.patientId)
            findNavController().navigate(action)
        }

        binding.btnToolMacros.setOnClickListener {
            val action = PatientProfileFragmentDirections.actionPatientProfileToMacroCalculator(args.patientId)
            findNavController().navigate(action)
        }

        binding.btnToolWater.setOnClickListener {
            val action = PatientProfileFragmentDirections.actionPatientProfileToWaterCalculator(args.patientId)
            findNavController().navigate(action)
        }

        binding.btnToolEspen.setOnClickListener {
            val action = PatientProfileFragmentDirections.actionPatientProfileToEspenCalculator(args.patientId)
            findNavController().navigate(action)
        }
    }
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.patientState.collectLatest { patient ->
                    patient?.let { bind(it) }
                }
            }
        }
    }

    private fun bind(patient: Patient) {
        binding.tvPatientName.text = "${patient.name} ${patient.paternalLN} ${patient.maternalLN}"
        binding.tvBirthday.text = patient.birthday?.let { dateFormat.format(it) } ?: getString(R.string.n_a)
        binding.tvGender.text = patient.gender ?: getString(R.string.n_a)
        binding.tvWeight.text = patient.weight?.let { "$it kg" } ?: getString(R.string.n_a)
        binding.tvHeight.text = patient.height?.let { "$it cm" } ?: getString(R.string.n_a)
        binding.tvNotes.text = patient.notes?.takeIf { it.isNotEmpty() } ?: getString(R.string.no_hay_notas)

        val age = patient.birthday?.let { getAge(it) }
        binding.tvPatientAge.text = age?.let { getString(R.string.a_os, it) } ?: getString(R.string.edad_no_disponible)
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
