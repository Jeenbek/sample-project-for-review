package com.missit.ui.auth.signIn

import com.missit.base.BaseViewModel
import com.missit.extensions.load
import com.missit.manager.SharedPreferencesManager
import com.missit.model.NameAndEmail
import com.missit.model.SignIn
import com.missit.model.VerifyData
import com.missit.model.VerifyResponse
import com.missit.network.Api
import com.missit.network.handle
import timber.log.Timber

class SignInViewModel(
    private val api: Api,
    private val preferencesManager: SharedPreferencesManager
) : BaseViewModel() {
    fun signIn(email: String, password: String) = vmScope.load {
        api.signIn(SignIn(email, password)).handle({
            it.data.token?.let { token ->
                preferencesManager.token = token
                router.newRootScreen(screens.mainFlow())
            }

            it.data.password_token?.let { token ->
                preferencesManager.passwordToken = token
                it.data.name?.let {name ->
                    router.newRootScreen(
                        screens.setPassword(
                            VerifyResponse(
                                "", VerifyData(
                                    token,
                                    NameAndEmail(name, email)
                                )
                            )
                        )
                    )
                }
            }
        }, {
            Timber.e(it)
        })
    }

    fun forgotPassword() = router.navigateTo(screens.forgot())
}