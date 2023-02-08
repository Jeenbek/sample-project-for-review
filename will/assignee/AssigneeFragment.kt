package com.missit.ui.will.assignee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.Input
import com.missit.custom.viewBinding
import com.missit.databinding.FragmentAssigneeBinding
import com.missit.databinding.ItemAddAssigneeBinding
import com.missit.databinding.ItemAssigneeBinding
import com.missit.databinding.SheetAddMemberBinding
import com.missit.extensions.*
import com.missit.model.Assignee
import com.missit.model.AssigneeAdd
import com.missit.model.WillIt

class AssigneeFragment : BaseFragment<AssigneeViewModel>(R.layout.fragment_assignee) {
    private val binding by viewBinding(FragmentAssigneeBinding::bind)
    private val will by paramNotNull<WillIt>()
    private var itemId: Int? = null
    private val adapter by lazy {
        AssigneeAdapter {
            when (it) {
                is Assignee -> {
                    ItemAssigneeBinding.bind(this).apply {
                        mark.isVisible = it.selected
                        name.text = it.name
                        status.text = it.relationship
                    }
                    onClick {
                        selectAssignee(it)
                        itemId = it.id
                        binding.toolbarAssignee.rightItem.isEnabled = true
                    }
                }
                is AssigneeAdd -> {
                    ItemAddAssigneeBinding.bind(this).apply {
                        addMember.onClick(::showAddMemberBottomSheet)
                    }
                }
            }
        }
    }

    private fun selectAssignee(selectedAssignee: Assignee) {
        val list = viewModel.assignees.value ?: emptyList()
        var prevAssigneeIndex: Int? = null
        var selectedAssigneeIndex: Int? = null

        list.forEachIndexed { index, assignee ->
            if (assignee.selected) {
                prevAssigneeIndex = index
            }
            if (assignee == selectedAssignee) {
                selectedAssigneeIndex = index
            }
        }

        if (selectedAssigneeIndex == prevAssigneeIndex) return

        list.onEachIndexed { index, item ->
            item.selected = index == selectedAssigneeIndex
        }
        prevAssigneeIndex?.let {
            adapter.notifyItemChanged(it)
        }
        selectedAssigneeIndex?.let {
            adapter.notifyItemChanged(it)
        }
    }

    private fun containsInvalidInputs(vararg input: Input): Boolean {
        input.forEach {
            it.error = if (it.text.isNullOrBlank()) "Enter ".plus(it.label) else null
        }
        return input.any { it.error != null }
    }

    private fun showAddMemberBottomSheet() {
        showBottomSheet {
            SheetAddMemberBinding.inflate(
                LayoutInflater.from(it.context),
                it.requireView() as ViewGroup,
                true
            ).apply {
                save.onClick {
                    containsInvalidInputs(fullName, relationship).let { containsError ->
                        if (!containsError) {
                            viewModel.createAssignee(
                                fullName.text.toString(),
                                relationship.text.toString()
                            )
                            it.dismiss()
                        }
                    }
                }
                close.onClick(it::dismiss)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbarAssignee.apply {
                leftItem.setBackButton(viewModel::exit)
                title.text = getString(R.string.assignee)
                rightItem.apply {
                    setSaveButton {
                        itemId?.let {
                            viewModel.save(it, will.id)
                        }
                    }
                    isEnabled = false
                }
                viewModel.saveButtonSate.observe {
                }
            }
            assignees.adapter = adapter
        }
        viewModel.assignees.observe {
            val listWithAddAssignee = it + listOf(AssigneeAdd("Add member"))
            adapter.setList(listWithAddAssignee)
            it.forEach { assignee ->
                if (will.inheritor_id != null && will.inheritor_id == assignee.id) {
                    selectAssignee(assignee)
                }
            }
        }
        viewModel.passcodeError.observe {
            if (it) {
                viewModel.passcodeError.post(false)
                passcode { viewModel.getPasscodeToken(it) }
            }
        }
    }
}