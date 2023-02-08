package com.missit.ui.will

import com.missit.base.BaseViewModel
import com.missit.custom.PrivateLiveData
import com.missit.extensions.load
import com.missit.manager.SharedPreferencesManager
import com.missit.model.*
import com.missit.network.Api
import com.missit.network.ErrorBody
import com.missit.network.handle
import kotlinx.coroutines.launch

class WillViewModel(
    private val api: Api,
    private val preferencesManager: SharedPreferencesManager,
) : BaseViewModel() {
    fun willDetail(will: WillIt) = appRouter.navigateTo(screens.willDetail(will))
    val willIt = PrivateLiveData<WillItResponse?>()
    val passcodeError = PrivateLiveData<Boolean>()
    val filterProperty = PrivateLiveData<List<FilterProperty>>()
    val filterStatus = PrivateLiveData<FilterStatus>()
    val assignees = PrivateLiveData<List<Assignee>>()

    init {
        lock()
        getFilterProperty()
        getFilterStatus()
        getAssigneeList()
    }

    fun getWillList(
        itemId: Int? = null,
        name: String? = null,
        inheritorId: Int? = null,
        assigned: Boolean? = null,
    ) = vmScope.load {
        api.willIt(
            preferencesManager.passwordToken,
            itemId = itemId,
            name = name,
            inheritorId = inheritorId,
            assigned = assigned
        )
            .handle({
                willIt.post(it)
            }, {
                when ((it as ErrorBody).code) {
                    419 -> passcodeError.post(true)
                    403 -> router.navigateTo(screens.profileChangeNewPasscode())
                }
            })
    }

    fun getPasscodeToken(passcode: String) = vmScope.load {
        api.inputPasscode(mapOf("passcode" to passcode)).handle({
            preferencesManager.passwordToken = it.data.passcode_token
            getWillList()
        }, {
            passcodeError.post(true)
        })
    }

    private fun getFilterProperty() = vmScope.launch {
        api.getFilterProperty().handle({
            filterProperty.post(it.data)
        })
    }

    private fun getFilterStatus() = vmScope.launch {
        api.getFilterAssignee().handle({
            filterStatus.post(it.data)
        })
    }

    private fun getAssigneeList() = vmScope.load {
        api.getRelatives().handle({
            assignees.post(it)
        })
    }

    fun lock() {
        preferencesManager.passwordToken = null
        willIt.post(null)
        getWillList()
    }
}