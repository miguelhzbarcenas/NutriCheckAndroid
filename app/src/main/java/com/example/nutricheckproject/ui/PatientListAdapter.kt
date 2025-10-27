package com.example.nutricheckproject.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutricheckproject.data.Patient
import com.example.nutricheckproject.databinding.ItemPatientBinding
import java.util.UUID

class PatientListAdapter(
    private val onItemClicked: (UUID) -> Unit
) : ListAdapter<Patient, PatientListAdapter.PatientViewHolder>(PatientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = getItem(position)
        holder.bind(patient)
        holder.itemView.setOnClickListener {
            onItemClicked(patient.id)
        }
    }

    class PatientViewHolder(private val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(patient: Patient) {

            val fullName = "${patient.name} ${patient.paternalLN} ${patient.maternalLN}"
            binding.tvPatientName.text = fullName
            binding.tvPatientDetails.text = "ID: ${patient.id}"
        }
    }
}

class PatientDiffCallback : DiffUtil.ItemCallback<Patient>() {
    override fun areItemsTheSame(oldItem: Patient, newItem: Patient): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Patient, newItem: Patient): Boolean {
        return oldItem == newItem
    }
}
