package com.missit.ui.will.detail

import android.os.Bundle
import android.view.View
import coil.load
import com.missit.R
import com.missit.base.BaseFragment
import com.missit.custom.viewBinding
import com.missit.databinding.FragmentWillDetailBinding
import com.missit.extensions.decimalFormat
import com.missit.extensions.onClick
import com.missit.extensions.setBackButton
import com.missit.model.WillIt

class WillDetailFragment : BaseFragment<WillDetailViewModel>(R.layout.fragment_will_detail) {
    private val binding by viewBinding(FragmentWillDetailBinding::bind)
    private val will by paramNotNull<WillIt>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            toolbarDetail.apply {
                title.text = will.name
                leftItem.setBackButton(viewModel::exit)
            }

            image.load(will.preview_img)
            itemName.text = will.name
            itemPrice.text = will.value.decimalFormat()

            assigneeLayout.onClick { viewModel.assignee(will) }
        }
    }
}