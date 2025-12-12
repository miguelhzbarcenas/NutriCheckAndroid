package com.example.nutricheckproject.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.nutricheckproject.R
import com.example.nutricheckproject.databinding.FragmentLoginBinding
import com.example.nutricheckproject.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkCurrentUser()

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            viewModel.login(email, pass)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            viewModel.register(email, pass)
        }

        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            viewModel.resetPassword(email)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.authState.collectLatest { state ->
                binding.progressBar.visibility = if (state is LoginViewModel.AuthState.Loading) View.VISIBLE else View.INVISIBLE

                when (state) {
                    is LoginViewModel.AuthState.Success -> {
                        navigateToHome()
                    }
                    is LoginViewModel.AuthState.Registered -> {
                        Toast.makeText(context, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                    is LoginViewModel.AuthState.PasswordResetSent -> {
                        Toast.makeText(context, getString(R.string.reset_email_sent), Toast.LENGTH_LONG).show()
                    }
                    is LoginViewModel.AuthState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToPatientListFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}