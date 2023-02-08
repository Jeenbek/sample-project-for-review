package com.missit.ui.auth.signIn

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.viewBinding
import com.missit.databinding.FragmentSignInBinding
import com.missit.extensions.hidePassword
import com.missit.extensions.isInvalidEmail
import com.missit.extensions.isInvalidPassword
import com.missit.extensions.onClick

class SignInFragment : BaseFragment<SignInViewModel>(R.layout.fragment_sign_in) {

    private val binding by viewBinding(FragmentSignInBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        with(binding) {
            login.onClick(::invalidateInputs)
            password.hidePassword()

            reset.onClick(viewModel::forgotPassword)
        }
    }

    private fun invalidateInputs() = with(binding) {
        email.error = when {
            email.text.isNullOrBlank() -> getString(R.string.enter_email)
            email.text.isInvalidEmail() -> getString(R.string.enter_valid_email)
            else -> null
        }

        password.error = when {
            password.text.isNullOrBlank() -> getString(R.string.enter_password)
            password.text.isInvalidPassword() -> getString(R.string.password_hint)
            else -> null
        }

        if (email.error == null && password.error == null)
            viewModel.signIn(email.text.toString(), password.text.toString())
    }
}