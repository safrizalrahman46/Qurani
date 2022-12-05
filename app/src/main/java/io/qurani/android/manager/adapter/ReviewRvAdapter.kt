package io.qurani.android.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.qurani.android.databinding.ItemReviewBinding
import io.qurani.android.manager.Utils
import io.qurani.android.model.Review

class ReviewRvAdapter(reviews: List<Review>) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<Review, ReviewRvAdapter.ViewHolder>(reviews) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val review = items[position]
        val context = viewholder.itemView.context

        viewholder.binding.itemReviewTv.text = review.description
        viewholder.binding.itemReviewDateTv.text = Utils.getDateFromTimestamp(review.createdAt)

        val user = review.user
        viewholder.binding.itemReviewUserNameTv.text = user?.name
        viewholder.binding.itemReviewUserRatingBar.rating = review.rating
        if (user?.avatar != null) {
            Glide.with(context).load(user.avatar).into(viewholder.binding.itemReviewUserImg)
        }
    }

    inner class ViewHolder(val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)
}