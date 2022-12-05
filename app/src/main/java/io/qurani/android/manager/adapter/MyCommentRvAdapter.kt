package io.qurani.android.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.qurani.android.databinding.ItemMyCommentBinding
import io.qurani.android.manager.Utils
import io.qurani.android.model.Comment

class MyCommentRvAdapter(comments: List<Comment>) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<Comment, MyCommentRvAdapter.ViewHolder>(comments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val comment = items[position]
        viewholder.binding.itemMyCommentDateTimeTv.text =
            Utils.getDateTimeFromTimestamp(comment.createdAt)

        val img: String?

        if (comment.blog != null) {
            viewholder.binding.itemMyCommentTitleTv.text = comment.blog!!.title
            img = comment.blog!!.image
        } else {
            viewholder.binding.itemMyCommentTitleTv.text = comment.course!!.title
            img = comment.course!!.img
        }

        if (img != null) {
            Glide.with(viewholder.itemView.context).load(img)
                .into(viewholder.binding.itemMyCommentImg)
        }

    }

    class ViewHolder(val binding: ItemMyCommentBinding) :
        RecyclerView.ViewHolder(binding.root)
}