package com.missit.ui.auth.forgot

import android.os.Bundle
import android.view.View
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.viewBinding
import com.missit.databinding.FragmentForgotBinding
import com.missit.extensions.onClick

class ForgotFragment : BaseFragment<ForgotViewModel>(R.layout.fragment_forgot) {
    val binding by viewBinding(FragmentForgotBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            next.onClick {
                viewModel.sendCodeForEmail(email.text.toString())
            }
        }
    }
}