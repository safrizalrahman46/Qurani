package io.qurani.android.manager.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.qurani.android.R
import io.qurani.android.databinding.ItemTextViewBinding
import io.qurani.android.manager.Utils
import io.qurani.android.model.Category
import kotlin.math.roundToInt

class BlogCategoriesRvAdapter(categories: List<Category>) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<Category, BlogCategoriesRvAdapter.ViewHolder>(categories) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTextViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val category = items[position]
        viewholder.binding.tv.text = category.title
    }

    inner class ViewHolder(val binding: ItemTextViewBinding) :
        RecyclerView.ViewHolder(binding.root){
        init {
            val margin = Utils.changeDpToPx(itemView.context, 16f).roundToInt()
            val params = binding.tv.layoutParams as LinearLayout.LayoutParams
            params.marginStart = margin
            params.marginEnd = margin
            binding.tv.requestLayout()

            binding.tvContainer.isFocusable = true
            binding.tvContainer.isClickable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.tvContainer.foreground = ContextCompat.getDrawable(itemView.context, R.drawable.ripple_effect)
            }
        }
    }
}