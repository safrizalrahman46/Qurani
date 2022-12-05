package io.qurani.android.manager.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.qurani.android.R
import io.qurani.android.databinding.ItemStudentBinding
import io.qurani.android.manager.Utils
import io.qurani.android.model.User

class StudentRvAdapter(users: List<User>, private val isClickable: Boolean = false) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<User, StudentRvAdapter.ViewHolder>(users) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStudentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val user = items[position]
        val context = viewholder.itemView.context

        val binding = viewholder.binding

        if (isClickable) {
            binding.itemStudentCertContainer.isFocusable = true
            binding.itemStudentCertContainer.isClickable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.itemStudentCertContainer.foreground =
                    ContextCompat.getDrawable(context, R.drawable.ripple_effect)
            }
        }

        if (user.avatar != null) {
            Glide.with(context).load(user.avatar)
                .into(binding.itemStudentCertImg)
        }

        binding.itemStudentCertNameTv.text = user.name
        binding.itemStudentCertDateTv.text = Utils.getDateFromTimestamp(user.date)
    }

    class ViewHolder(val binding: ItemStudentBinding) : RecyclerView.ViewHolder(binding.root)
}