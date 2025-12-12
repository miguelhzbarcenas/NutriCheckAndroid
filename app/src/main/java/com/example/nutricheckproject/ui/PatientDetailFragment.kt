package com.example.nutricheckproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.nutricheckproject.R
import com.example.nutricheckproject.data.Patient
import com.example.nutricheckproject.databinding.FragmentPatientDetailBinding
import com.example.nutricheckproject.viewmodel.PatientDetailViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class PatientDetailFragment : Fragment() {

    private var _binding: FragmentPatientDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PatientDetailFragmentArgs by navArgs()
    private val viewModel: PatientDetailViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    private var currentPatientId: UUID? =null
    private var creationDate: Date = Date()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val patientIdString = args.patientId
        if (patientIdString != getString(R.string.new_)) {
            try {
                val patientId = UUID.fromString(patientIdString)
                currentPatientId = patientId
                viewModel.loadPatient(patientId)
                binding.btnDelete.visibility = View.VISIBLE
            } catch (_: IllegalArgumentException) {
                Toast.makeText(context,
                    getString(R.string.error_id_de_paciente_inv_lido), Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        } else {
            binding.btnDelete.visibility = View.GONE
        }

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener { savePatient() }
        binding.btnDelete.setOnClickListener { showDeleteConfirmation() }
        binding.etBirthday.setOnClickListener { showDatePicker() }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirmar_eliminacion))
            .setMessage(getString(R.string.mensaje_confirmar_eliminar))
            .setPositiveButton(getString(R.string.eliminar)) { _, _ ->
                viewModel.deletePatient()
            }
            .setNegativeButton(getString(R.string.cancelar), null)
            .show()
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val constraintsBuilder = CalendarConstraints.Builder()
            .setEnd(today)

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.seleccionar_fecha_de_nacimiento))
            .setSelection(today)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date(selection)
            binding.etBirthday.setText(dateFormat.format(date))
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.patientState.collectLatest { patient ->
                    patient?.let { bind(it) }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.finishEvent.collect { didFinish ->
                    if (didFinish) {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteSuccess.collect { deleted ->
                    if (deleted) {
                        Toast.makeText(context, getString(R.string.paciente_eliminado), Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack(R.id.patientListFragment, false)
                    }
                }
            }
        }
    }

    private fun bind(patient: Patient) {
        currentPatientId = patient.id
        creationDate = patient.creation

        binding.etName.setText(patient.name)
        binding.etPaternalLn.setText(patient.paternalLN)
        binding.etMaternalLn.setText(patient.maternalLN)

        when (patient.gender) {
            getString(R.string.masculino) -> binding.toggleGender.check(R.id.btn_male)
            getString(R.string.femenino) -> binding.toggleGender.check(R.id.btn_female)
            else -> binding.toggleGender.clearChecked()
        }

        binding.etWeight.setText(patient.weight?.toString() ?: "")
        binding.etHeight.setText(patient.height?.toString() ?: "")
        binding.etNotes.setText(patient.notes ?: "")

        patient.birthday?.let {
            binding.etBirthday.setText(dateFormat.format(it))
        } ?: binding.etBirthday.setText("")
    }

    private fun savePatient() {
        val name = binding.etName.text.toString().trim()
        val paternalLN = binding.etPaternalLn.text.toString().trim()
        val maternalLN = binding.etMaternalLn.text.toString().trim()

        if (name.isEmpty() || paternalLN.isEmpty()) {
            Toast.makeText(context,
                getString(R.string.nombre_y_apellido_paterno_son_obligatorios), Toast.LENGTH_SHORT).show()
            return
        }

        val dateString = binding.etBirthday.text.toString()
        if (dateString.isEmpty()) {
            Toast.makeText(context,
                getString(R.string.la_fecha_de_nacimiento_es_obligatoria), Toast.LENGTH_SHORT).show()
            return
        }

        val birthday: Date? = try {
            dateFormat.parse(dateString)
        } catch (_: Exception) {
            Toast.makeText(context,
                getString(R.string.formato_de_fecha_inv_lido), Toast.LENGTH_SHORT).show()
            return
        }

        if (birthday != null) {
            val todayCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            todayCal.set(Calendar.HOUR_OF_DAY, 0)
            todayCal.set(Calendar.MINUTE, 0)
            todayCal.set(Calendar.SECOND, 0)
            todayCal.set(Calendar.MILLISECOND, 0)
            val today = todayCal.time

            if (birthday.after(today)) {
                Toast.makeText(context,
                    getString(R.string.la_fecha_de_nacimiento_no_puede_ser_en_el_futuro), Toast.LENGTH_SHORT).show()
                return
            }
        }

        val gender = when (binding.toggleGender.checkedButtonId) {
            R.id.btn_male -> "Masculino"
            R.id.btn_female -> "Femenino"
            else -> null
        }
        if (gender == null) {
            Toast.makeText(context,
                getString(R.string.por_favor_seleccione_un_g_nero), Toast.LENGTH_SHORT).show()
            return
        }

        val weightStr = binding.etWeight.text.toString()
        if (weightStr.isBlank()) {
            Toast.makeText(context, getString(R.string.el_peso_es_obligatorio), Toast.LENGTH_SHORT).show()
            return
        }
        val weight = weightStr.toDoubleOrNull()
        if (weight == null || weight <= 0) {
            Toast.makeText(context,
                getString(R.string.por_favor_ingrese_un_peso_v_lido), Toast.LENGTH_SHORT).show()
            return
        }

        val heightStr = binding.etHeight.text.toString()
        if (heightStr.isBlank()) {
            Toast.makeText(context, getString(R.string.la_altura_es_obligatoria), Toast.LENGTH_SHORT).show()
            return
        }
        val height = heightStr.toDoubleOrNull()
        if (height == null || height <= 0) {
            Toast.makeText(context,
                getString(R.string.por_favor_ingrese_una_altura_v_lida), Toast.LENGTH_SHORT).show()
            return
        }

        val notes = binding.etNotes.text.toString().trim().takeIf { it.isNotEmpty() }

        val patientToSave = Patient(
            id = currentPatientId ?: UUID.randomUUID(),
            name = name,
            paternalLN = paternalLN,
            maternalLN = maternalLN,
            birthday = birthday,
            gender = gender,
            height = height,
            weight = weight,
            notes = notes,
            creation = creationDate
        )

        viewModel.savePatient(patientToSave)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
