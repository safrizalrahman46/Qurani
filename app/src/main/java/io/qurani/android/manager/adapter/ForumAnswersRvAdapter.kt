package io.qurani.android.manager.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import io.qurani.android.R
import io.qurani.android.databinding.ItemForumAnswerBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.Utils
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.model.ForumItem
import io.qurani.android.model.ForumItemAnswer
import io.qurani.android.model.User
import io.qurani.android.ui.frag.ForumAnswersFrag
import io.qurani.android.ui.widget.ForumOptionsDialog
import io.qurani.android.ui.widget.ProgressiveLoadingDialog
import java.io.File

class ForumAnswersRvAdapter(
    forumItems: List<ForumItemAnswer>,
    val fragment: ForumAnswersFrag,
    val callback: ItemCallback<Any>
) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<ForumItemAnswer, ForumAnswersRvAdapter.ViewHolder>(forumItems) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemForumAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val item = items[position]
        val context = viewholder.itemView.context
        val binding = viewholder.binding

        val user = item.user
        if (!user.avatar.isNullOrEmpty()) {
            Glide.with(context).load(user.avatar).into(binding.itemForumAnswerUserImg)
        }

        if (!io.qurani.android.manager.App.loggedInUser!!.isUser()) {
            binding.itemForumAnswerIntrcutorTypeCircle.visibility = View.VISIBLE
        }

        setUserRole(context, user, binding.itemForumAnswerUserTypeTv)
        binding.itemForumUserNameTv.text = user.name
        binding.itemForumAnswerDateTimeTv.text = Utils.getDateTimeFromTimestamp(item.createdAt)
        binding.itemForumAnswerDescTv.text = item.description


        // First position is reserved for question
        if (position > 0) {
            viewholder.init(item)

            if (item.isPinned) {
                binding.itemForumAnswerPinImg.visibility = View.VISIBLE
            }

            if (item.resolved) {
                binding.itemForumAnswerResolvedView.visibility = View.VISIBLE
            }
        } else if (item.attachment != null) {
            val fileName = Utils.extractFileNameFromUrl(item.attachment!!)
            binding.itemForumAnswerAttachmentDescTv.text = fileName
            binding.itemForumAnswerAttachmentContainer.visibility = View.VISIBLE
            binding.itemForumAnswerAttachmentContainer.setOnClickListener {
                if (fragment.hasPermission()) {
                    downloadFile(context, item.attachment!!)
                } else {
                    fragment.requestPermission()
                }
            }
        }
    }

    fun downloadFile(context: Context, url: String) {
        val bundle = Bundle()
        bundle.putString(io.qurani.android.manager.App.URL, url)
        bundle.putString(io.qurani.android.manager.App.DIR, io.qurani.android.manager.App.Companion.Directory.FORUMS_ATTACHMENT.value())
        bundle.putBoolean(io.qurani.android.manager.App.TO_DOWNLOADS, true)

        val loadingDialog = ProgressiveLoadingDialog()
        loadingDialog.setOnFileSavedListener(object : ItemCallback<String> {
            override fun onItem(filePath: String, vararg args: Any) {
                Utils.viewFile(context, File(filePath))
            }
        })
        loadingDialog.arguments = bundle
        loadingDialog.show(fragment.childFragmentManager, null)
    }

    private fun setUserRole(context: Context, user: User, userTypeTv: MaterialTextView) {
        var txt = ""

        when (user.roleName) {
            User.Role.INSTRUCTOR.value() -> {
                txt = context.getString(R.string.instructor)
            }

            User.Role.ORGANIZATION.value() -> {
                txt = context.getString(R.string.organization)
            }

            User.Role.USER.value() -> {
                txt = context.getString(R.string.user)
            }
        }

        userTypeTv.text = txt
    }


    inner class ViewHolder(val binding: ItemForumAnswerBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        fun init(item: ForumItem) {
            if (item.can.pin || item.can.update || item.can.resolve) {
                binding.itemForumMoreBtn.visibility = View.VISIBLE
                binding.itemForumMoreBtn.setOnClickListener(this)
            }
        }

        override fun onClick(v: View?) {
            val item = items[bindingAdapterPosition]

            val bundle = Bundle()
            bundle.putParcelable(io.qurani.android.manager.App.ITEM, item)

            val dialog = ForumOptionsDialog()
            dialog.setOnSuccessListener(callback)
            dialog.arguments = bundle
            dialog.show(fragment.childFragmentManager, null)
        }

    }
}