package com.missit.ui.auth.newPassword

import com.missit.base.BaseViewModel
import com.missit.extensions.load
import com.missit.network.Api
import com.missit.network.handle
import timber.log.Timber

class NewPasswordViewModel(private val api: Api) : BaseViewModel() {
    fun setPassword(newPassword: String, verifyToken: String) =
        vmScope.load {
            api.setPassword(mapOf("password" to newPassword, "password_token" to verifyToken))
                .handle({
                    appRouter.newRootScreen(screens.authFlow())
                }, {
                    Timber.e(it)
                })
        }
    fun login() = appRouter.newRootChain(screens.authFlow())
}