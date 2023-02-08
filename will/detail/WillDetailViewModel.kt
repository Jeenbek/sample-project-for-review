package com.missit.ui.will.detail

import com.missit.base.BaseViewModel
import com.missit.model.Will
import com.missit.model.WillIt

class WillDetailViewModel : BaseViewModel() {
    fun assignee(will: WillIt) = appRouter.navigateTo(screens.assignee(will))


}