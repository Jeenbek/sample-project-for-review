package com.missit.ui.will.assignee

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.missit.extensions.inflate
import com.missit.model.BaseAdapterModel
import kotlinx.android.extensions.LayoutContainer

class AssigneeAdapter(
    var items: List<BaseAdapterModel> = listOf(),
    private val bind: View.(BaseAdapterModel) -> Unit = {}
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setList(items: List<BaseAdapterModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(parent.inflate(viewType))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as? ViewHolder)?.bind(items[position]) ?: Unit

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].getLayout()

    inner class ViewHolder(override val containerView: View):
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(it: BaseAdapterModel) = containerView.bind(it)
    }
}