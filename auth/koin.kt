package com.missit.ui.auth

import com.missit.ui.auth.forgot.ForgotViewModel
import com.missit.ui.auth.newPassword.NewPasswordViewModel
import com.missit.ui.auth.signIn.SignInViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel<SignInViewModel>()
    viewModel<NewPasswordViewModel>()
    viewModel<ForgotViewModel>()
}