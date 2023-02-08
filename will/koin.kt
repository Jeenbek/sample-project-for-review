package com.missit.ui.will

import com.missit.ui.will.assignee.AssigneeViewModel
import com.missit.ui.will.detail.WillDetailViewModel
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.dsl.module

val willModule = module {
    viewModel<WillViewModel>()
    viewModel<WillDetailViewModel>()
    viewModel<AssigneeViewModel>()
}