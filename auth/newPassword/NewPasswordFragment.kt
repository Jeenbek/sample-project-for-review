package com.missit.ui.auth.newPassword

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.viewBinding
import com.missit.databinding.FragmentNewPasswordBinding
import com.missit.extensions.hidePassword
import com.missit.extensions.isInvalidPassword
import com.missit.extensions.onClick
import com.missit.model.VerifyResponse

class NewPasswordFragment : BaseFragment<NewPasswordViewModel>(R.layout.fragment_new_password) {
    private val binding by viewBinding(FragmentNewPasswordBinding::bind)

    private val verifyResponse by paramNotNull<VerifyResponse>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            set.onClick(::setPassword)
            newPassword.hidePassword()
            repeatPassword.hidePassword()
            login.onClick(viewModel::login)
            email.isVisible = verifyResponse.data.nameAndEmail != null
            name.isVisible = verifyResponse.data.nameAndEmail != null

            email.text = verifyResponse.data.nameAndEmail?.email
            name.text = verifyResponse.data.nameAndEmail?.name

        }
    }

    private fun setPassword() = with(binding) {
        newPassword.error = when {
            newPassword.text.isNullOrBlank() -> getString(R.string.enter_password)
            newPassword.text.isInvalidPassword() -> getString(R.string.password_hint)
            else -> null
        }

        repeatPassword.error = when {
            repeatPassword.text.isNullOrBlank() -> getString(R.string.enter_password)
            repeatPassword.text.isInvalidPassword() -> getString(R.string.password_hint)
            newPassword.text.toString() != repeatPassword.text.toString() -> getString(R.string.password_mismatch)
            else -> null
        }

        if (newPassword.error != null || repeatPassword.error != null) {
            if (newPassword.text != repeatPassword.text) {
                repeatPassword.error = getString(R.string.password_do_not_match)
            }
            return@with
        }
        viewModel.setPassword(
            newPassword.text.toString(),
            verifyResponse.data.password_token
        )
    }
}