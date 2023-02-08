package com.missit.ui.will.assignee

import com.missit.base.BaseViewModel
import com.missit.custom.PrivateLiveData
import com.missit.custom.SingleLiveEvent
import com.missit.extensions.load
import com.missit.manager.SharedPreferencesManager
import com.missit.model.Assignee
import com.missit.network.Api
import com.missit.network.ErrorBody
import com.missit.network.handle

class AssigneeViewModel(private val api: Api, private val preferences: SharedPreferencesManager) :
    BaseViewModel() {

    val saveButtonSate = SingleLiveEvent<Boolean>()

    val assignees = PrivateLiveData<List<Assignee>>()

    val passcodeError = PrivateLiveData<Boolean>()


    init {
        getAssigneeList()
    }

    fun save(relativeId: Int, itemId: Int) = vmScope.load {
        preferences.passwordToken?.let {
            api.saveAssignee(relativeId, itemId, it).handle({
                appRouter.exit()
            })
        }
    }


    fun createAssignee(name: String, relationship: String) = vmScope.load {
        preferences.passwordToken?.let {
            api.createRelatives(
                mapOf(
                    "name" to name,
                    "relationship" to relationship,
                    "passcode_token" to it
                )
            ).handle({
                getAssigneeList()
            }, {
                when ((it as ErrorBody).code) {
                    419 -> passcodeError.post(true)
                    403 -> router.navigateTo(screens.profileChangeNewPasscode())
                }
            })
        }
    }


    private fun getAssigneeList() = vmScope.load {
            api.getRelatives().handle({
                assignees.post(it)
            }, {
                when ((it as ErrorBody).code) {
                    419 -> passcodeError.post(true)
                    403 -> router.navigateTo(screens.profileChangeNewPasscode())
                }
            })

    }

    fun getPasscodeToken(passcode: String) = vmScope.load {
        api.inputPasscode(mapOf("passcode" to passcode)).handle({
            preferences.passwordToken = it.data.passcode_token
            getAssigneeList()
        }, {
            passcodeError.post(true)
        })
    }

}