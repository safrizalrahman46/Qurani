package io.qurani.android.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.qurani.android.R
import io.qurani.android.databinding.DialogCommentMoreBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog
import io.qurani.android.model.Comment
import io.qurani.android.ui.MainActivity

class CommentMoreDialog : io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogCommentMoreBinding
    private lateinit var mComment: Comment
    private var mItemCallback: ItemCallback<Comment>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogCommentMoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    fun setCallback(callback: ItemCallback<Comment>) {
        mItemCallback = callback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mComment = requireArguments().getParcelable(io.qurani.android.manager.App.COMMENT)!!

        mBinding.commentMoreCancelBtn.setOnClickListener(this)
        mBinding.commentMoreReportBtn.setOnClickListener(this)
        mBinding.commentMoreReplyBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.commentMoreCancelBtn -> {
                dismiss()
            }

            R.id.commentMoreReplyBtn, R.id.commentMoreReportBtn -> {
                if (!io.qurani.android.manager.App.isLoggedIn()) {
                    (activity as MainActivity).goToLoginPage(null)
                    return
                }

                var type = CommentDialog.Type.REPLY
                if (v.id == R.id.commentMoreReportBtn)
                    type = CommentDialog.Type.REPORT_COMMENT

                val bundle = Bundle()
                bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, type)
                bundle.putParcelable(io.qurani.android.manager.App.COMMENT, mComment)
                bundle.putInt(io.qurani.android.manager.App.ID, mComment.id)
                val reportDialog = CommentDialog()
//                mItemCallback?.let { reportDialog.setOnCommentSavedListener(it) }
                reportDialog.arguments = bundle
                reportDialog.show(parentFragmentManager, null)

                dismiss()
            }
        }
    }
}