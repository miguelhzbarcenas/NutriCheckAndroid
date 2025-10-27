package com.example.nutricheckproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nutricheckproject.R
import com.google.android.material.snackbar.Snackbar
import com.example.nutricheckproject.databinding.FragmentPatientListBinding
import com.example.nutricheckproject.viewmodel.PatientListViewModel
import com.example.nutricheckproject.viewmodel.PatientViewModelFactory
import kotlinx.coroutines.launch

class PatientListFragment : Fragment() {

    private var _binding: FragmentPatientListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientListViewModel by viewModels {
        PatientViewModelFactory(requireActivity().application)
    }

    private lateinit var patientListAdapter: PatientListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSwipeToDelete()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allPatients.collect { patients ->
                    patientListAdapter.submitList(patients)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        patientListAdapter = PatientListAdapter { patientId ->
            val action = PatientListFragmentDirections.actionPatientListToPatientProfile(
                patientId.toString()
            )
            findNavController().navigate(action)
        }

        binding.recyclerViewPatients.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewPatients.adapter = patientListAdapter
    }

    private fun setupClickListeners() {
        binding.fabAddPatient.setOnClickListener {
            val action = PatientListFragmentDirections
                .actionPatientListToPatientDetail("new")
            findNavController().navigate(action)
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val patient = patientListAdapter.currentList[position]
                viewModel.delete(patient)

                Snackbar.make(binding.root,
                    getString(R.string.paciente_eliminado), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.recyclerViewPatients)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
