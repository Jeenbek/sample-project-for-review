package com.missit.ui.auth.forgot

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import com.fraggjkee.smsconfirmationview.SmsConfirmationView
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.viewBinding
import com.missit.databinding.FragmentEmailVerifyBinding
import com.missit.extensions.hideKeyboard
import com.missit.extensions.onClick
import com.missit.extensions.show
import com.missit.model.ResetPasswordResponse

class ForgotVerifyCodeFragment : BaseFragment<ForgotViewModel>(R.layout.fragment_email_verify) {
    val binding by viewBinding(FragmentEmailVerifyBinding::bind)
    private val resetPasswordResponse by paramNotNull<ResetPasswordResponse>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timer.start()
        binding.toolbarCodeVerify.apply {
            leftItem.apply {
                show()
                setText(R.string.cancel)
                onClick(viewModel::exit)
            }
            title.setText(R.string.verification)
        }

        binding.apply {
            smsCodeView.onChangeListener =
                SmsConfirmationView.OnChangeListener { code, isComplete ->
                    done.onClick {
                        viewModel.verifyCode(
                            resetPasswordResponse.data.verify_token,
                            code
                        )
                    }
                }
            email.text = resetPasswordResponse.data.email

            viewModel.timer.message.observe { resent.text = it }
            viewModel.timer.finished.observe { resent.isEnabled = it }
            resent.onClick {
                viewModel.sendCodeForEmail(resetPasswordResponse.data.email)
                viewModel.timer.start()
            }
        }
    }
}