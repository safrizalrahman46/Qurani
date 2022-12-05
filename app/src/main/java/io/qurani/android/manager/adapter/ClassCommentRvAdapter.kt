package io.qurani.android.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.qurani.android.databinding.ItemClassCommentBinding
import io.qurani.android.manager.Utils
import io.qurani.android.model.Comment

class ClassCommentRvAdapter(comments: List<Comment>) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<Comment, ClassCommentRvAdapter.ViewHolder>(comments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemClassCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val comment = items[position]

        if (comment.user?.avatar != null) {
            Glide.with(viewholder.itemView.context).load(comment.user?.avatar)
                .into(viewholder.binding.itemClassCommentImg)
        }

        viewholder.binding.itemClassCommentNameTv.text = comment.user?.name
        viewholder.binding.itemClassCommentDateTv.text =
            Utils.getDateFromTimestamp(comment.createdAt)
        if (comment.blog != null) {
            viewholder.binding.itemClassCommentTitleTv.text = comment.blog!!.title
        } else {
            viewholder.binding.itemClassCommentTitleTv.text = comment.course!!.title
        }
    }

    class ViewHolder(val binding: ItemClassCommentBinding) :
        RecyclerView.ViewHolder(binding.root)
}