package com.missit.ui.will

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.viewBinding
import com.missit.databinding.*
import com.missit.extensions.*
import com.missit.model.Assignee
import com.missit.model.FilterBase
import com.missit.model.FilterBase.FilterButton
import com.missit.model.FilterBase.FilterItem
import com.missit.model.FilterProperty
import com.missit.model.WillIt
import com.missit.ui.myDocs.adapter.SelectAssigneeFilterAdapter
import com.missit.ui.myDocs.adapter.SelectFilterAdapter


@SuppressLint("SetTextI18n")
class WillFragment : BaseFragment<WillViewModel>(R.layout.fragment_will) {

    private val binding by viewBinding(FragmentWillBinding::bind)
    private val adapter by adapter<WillIt>(R.layout.item_will_it) {
        val binding = ItemWillItBinding.bind(this)
        onClick { viewModel.willDetail(it) }
        with(binding) {
            title.text = it.name
            subTitle.text =
                if (it.propertyName != null) "${it.propertyName} > ${it.name}" else "Property"
            icon.load(it.preview_img)
            name.text = it.inheritor_name ?: "Not assigned"
            name.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (it.inheritor_id != null) R.color.dark_100 else R.color.grey_100
                )
            )
            status.text = it.inheritor_relationship
        }
    }

    private var filters = arrayListOf<FilterBase>(FilterButton("Filter"))

    private var propertySelect: FilterProperty? = null
    private var assigneeSelect: Assignee? = null
    private var statusAssigned: String? = null
    private var name: String? = null


    private val propertyAdapter by lazy {
        SelectFilterAdapter()
    }
    private val assignedAdapter by lazy {
        SelectAssigneeFilterAdapter()
    }
    private val filterAdapter by adapter<FilterBase>(R.layout.item_filter) {
        ItemFilterBinding.bind(this)
            .apply {
                when (it) {
                    is FilterButton -> {
                        filterTitle.setFilterButton(it.title)
                        onClick {
                            showBottomSheet {
                                SheetFilterBinding.inflate(
                                    LayoutInflater.from(requireContext()),
                                    it.requireView() as ViewGroup,
                                    true
                                ).apply {
                                    close.onClick { it.dismiss() }
                                    clearSelect.onClick {
                                        clearSelect.hide()
                                    }
                                    btnApply.onClick {
                                        refreshFilterAdapter()
                                        viewModel.getWillList(
                                            itemId = propertySelect?.id,
                                            inheritorId = assigneeSelect?.id,
                                            assigned = when (statusAssigned) {
                                                "Assigned" -> true
                                                "Not Assigned" -> false
                                                else -> null
                                            }
                                        )
                                        it.dismiss()
                                    }
                                    propertySelect?.name?.let {
                                        propertySubTitle.text = it
                                    }
                                    assigneeSelect?.name?.let {
                                        assigneeSubTitle.text = it
                                    }
                                    statusAssigned?.let {
                                        statusSubTitle.text = it
                                    }
                                    if (propertySelect != null || assigneeSelect != null || statusAssigned != null) {
                                        clearSelect.show()
                                    }
                                    clearSelect.onClick {
                                        propertySelect = null
                                        assigneeSelect = null
                                        statusAssigned = null
                                        filters = arrayListOf(FilterButton("Sort by"))
                                        viewModel.getWillList()
                                        it.dismiss()
                                    }

                                    property.onClick {
                                        showBottomSheet {
                                            SheetFilterPropertyBinding.inflate(
                                                LayoutInflater.from(requireContext()),
                                                it.requireView() as ViewGroup,
                                                true
                                            ).apply {
                                                back.onClick { it.dismiss() }
                                                viewModel.filterProperty.observe {
                                                    propertyAdapter.propertyList = it
                                                }
                                                propertyList.adapter = propertyAdapter
                                                propertyAdapter.onItemClick = { property ->
                                                    selectProperty(property)
                                                    propertySubTitle.text = property.name
                                                    clearSelect.show()
                                                    it.dismiss()
                                                }
                                            }
                                        }
                                    }
                                    assignee.onClick {
                                        showBottomSheet {
                                            SheetFilterAssignedBinding.inflate(
                                                LayoutInflater.from(requireContext()),
                                                it.requireView() as ViewGroup,
                                                true
                                            ).apply {
                                                back.onClick { it.dismiss() }
                                                viewModel.assignees.observe {
                                                    assignedAdapter.assigneeList = it
                                                }
                                                assignList.adapter = assignedAdapter
                                                assignedAdapter.onItemClick = { assignee ->
                                                    selectAssignee(assignee)
                                                    assigneeSubTitle.text = assignee.name
                                                    clearSelect.show()
                                                    it.dismiss()
                                                }
                                            }
                                        }
                                    }
                                    status.onClick {
                                        showBottomSheet {
                                            SheetFilterStatusBinding.inflate(
                                                LayoutInflater.from(requireContext()),
                                                it.requireView() as ViewGroup,
                                                true
                                            ).apply {
                                                back.onClick { it.dismiss() }
                                                viewModel.filterStatus.observe {
                                                    countAssigned.text =
                                                        String.format("${it.assigned} items")
                                                    countNotAssigned.text =
                                                        String.format("${it.not_assigned} items")
                                                }
                                                assigned.onClick {
                                                    selectStatus("Assigned")
                                                    statusSubTitle.text = "Assigned"
                                                    it.dismiss()
                                                    clearSelect.show()

                                                }
                                                notAssigned.onClick {
                                                    selectStatus("Not Assigned")
                                                    statusSubTitle.text = "Not Assigned"
                                                    it.dismiss()
                                                    clearSelect.show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is FilterItem -> filterTitle.setFilterItem(it.title)
                }
            }
    }

    private fun refreshFilterAdapter() {
        filterAdapter.submitList(filters)
        filterAdapter.notifyDataSetChanged()
    }

    private fun TextView.setFilterButton(title: String) {
        setBackgroundColor(getColor(R.color.white_100))
        setTextColor(getColor(R.color.green_100))
        setIcon(null, null, R.drawable.ic_arrow_down, null)
        text = title
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filterAdapter.submitList(filters)
        viewModel.willIt.observe {
            it?.let {
                binding.containerLock.show()
                with(binding) {
                    wills.adapter = adapter
                    filter.adapter = filterAdapter
                    if (it.data.isEmpty()) {
                        wills.hide()
                        empty.show()
                    } else {
                        wills.show()
                        empty.hide()
                        adapter.submitList(it.data)
                    }
                    refreshFilterAdapter()
                }
            } ?: run {
                with(binding) {
                    wills.hide()
                    empty.hide()
                }
            }
        }
        viewModel.passcodeError.observe {
            if (it) {
                binding.containerLock.hide()
                viewModel.passcodeError.post(false)
                passcode { viewModel.getPasscodeToken(it) }
            }
        }

        binding.search.editText?.searchText {
            name = if (it.isEmpty()) null else it
            viewModel.getWillList(
                itemId = propertySelect?.id,
                inheritorId = assigneeSelect?.id,
                name = name,
                assigned = when (statusAssigned) {
                    "Assigned" -> true
                    "Not Assigned" -> false
                    else -> null
                }
            )
        }
        binding.containerLock.onClick { viewModel.lock() }
    }

    private fun selectProperty(property: FilterProperty) {
        propertySelect = property
        var hasMatch = false
        filters.forEachIndexed { i, e ->
            if ((e as? FilterItem)?.filter == "property") {
                hasMatch = true
                filters[i] = FilterItem("property", property.name)
            }
        }
        if (!hasMatch) {
            hasMatch = false
            filters.add(
                FilterItem(
                    "property",
                    property.name
                )
            )
        }
    }

    private fun selectAssignee(assignee: Assignee) {
        assigneeSelect = assignee
        var hasMatch = false
        filters.forEachIndexed { i, e ->
            if ((e as? FilterItem)?.filter == "assignee") {
                hasMatch = true
                filters[i] = FilterItem("assignee", assignee.name)
            }
        }
        if (!hasMatch) {
            hasMatch = false
            filters.add(
                FilterItem(
                    "assignee",
                    assignee.name
                )
            )
        }
    }

    private fun selectStatus(status: String) {
        statusAssigned = status
        var hasMatch = false
        filters.forEachIndexed { i, e ->
            if ((e as? FilterItem)?.filter == "status") {
                hasMatch = true
                filters[i] = FilterItem("status", status)
            }
        }
        if (!hasMatch) {
            hasMatch = false
            filters.add(
                FilterItem(
                    "status",
                    status
                )
            )
        }
    }
}