package com.missit.ui.auth.forgot

import com.missit.base.BaseViewModel
import com.missit.custom.Input
import com.missit.extensions.getCode
import com.missit.extensions.load
import com.missit.model.VerifyData
import com.missit.model.VerifyResponse
import com.missit.network.Api
import com.missit.network.handle
import com.missit.utils.MSCountDownTimer
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ForgotViewModel(
    private val api: Api
) : BaseViewModel() {

    fun sendCodeForEmail(email: String) =
        vmScope.load {
            api.resetPassword(mapOf("email" to email)).handle({
                router.replaceScreen(screens.forgotVerify(it))
            }, {
                Timber.e(it)
            })
        }

    fun verifyCode(verifyToken: String, code: String) = vmScope.load {
        api.verifyCode(mapOf("verify_token" to verifyToken, "code" to code))
            .handle({
                router.replaceScreen(screens.setPassword(it))
            }, {
                Timber.e(it)
            })
    }


    val timer = MSCountDownTimer(120000L, {
        "Retry after ${SimpleDateFormat("mm:ss").format(Date(it))}"
    }, "Resend a Code")

    override fun exit() {
        router.exit()
    }
}