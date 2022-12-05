package io.qurani.android.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.qurani.android.databinding.ItemDialogSelectionBinding
import io.qurani.android.model.SelectionItem

class SelectionDialogRvAdapter(items: List<SelectionItem?>?) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<SelectionItem?, SelectionDialogRvAdapter.ViewHolder?>(
        items
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemDialogSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val title = items[position]?.title
        val img = items[position]?.img
        holder.binding.itemSelectionTv.text = "$title"
        if (img != null) {
            holder.binding.itemSelectionImg.setImageResource(img)
        } else {
            holder.binding.itemSelectionImg.visibility = View.GONE
        }
    }

    class ViewHolder(val binding: ItemDialogSelectionBinding) :
        RecyclerView.ViewHolder(binding.root)
}