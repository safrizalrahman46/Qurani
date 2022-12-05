package io.qurani.android.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.qurani.android.R
import io.qurani.android.databinding.DialogForumOptionsBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ForumItem
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.ForumOptionsPresenterImpl


class ForumOptionsDialog : io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogForumOptionsBinding
    private lateinit var mPresenter: Presenter.ForumOptionsPresenter
    private var mLoadingDialog: LoadingDialog? = null
    private lateinit var mForumItem: ForumItem
    private var mCallback: ItemCallback<Any>? = null

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogForumOptionsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mForumItem = requireArguments().getParcelable(io.qurani.android.manager.App.ITEM)!!

        mPresenter = ForumOptionsPresenterImpl(this)

        initUI()
        initForumItem()
    }

    private fun initForumItem() {
        if (mForumItem.can.update) {
            mBinding.forumOptionsEditBtn.visibility = View.VISIBLE
        }

        if (mForumItem.can.pin) {
            showPinItem()
        }

        if (mForumItem.isAnswer()) {
            if (mForumItem.can.resolve) {
                mBinding.forumOptionsMarkResovledBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun showPinItem() {
        mBinding.forumOptionsPinBtn.visibility = View.VISIBLE
        if (!mForumItem.isPinned) {
            mBinding.forumOptionsPinBtn.text = getString(R.string.unpin)
        }
    }

    private fun initUI() {
        mBinding.forumOptionsCancelBtn.setOnClickListener(this)
        mBinding.forumOptionsEditBtn.setOnClickListener(this)
        mBinding.forumOptionsMarkResovledBtn.setOnClickListener(this)
        mBinding.forumOptionsPinBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.forum_options_pin_btn -> {
                mLoadingDialog = LoadingDialog.instance
                mLoadingDialog!!.show(childFragmentManager, null)

                if (mForumItem.isAnswer()) {
                    mPresenter.pinForumItemAnswer(mForumItem.id)
                } else {
                    mPresenter.pinForumItem(mForumItem.id)
                }
            }

            R.id.forum_options_mark_resovled_btn -> {
                val bundle = Bundle()
                bundle.putString(io.qurani.android.manager.App.TITLE, getString(R.string.mark_as_resolved))
                bundle.putString(
                    io.qurani.android.manager.App.TEXT,
                    getString(R.string.are_you_sure_you_want_to_mark_this)
                )

                val dialog = AppDialog.instance
                dialog.arguments = bundle
                dialog.setOnDialogBtnsClickedListener(
                    AppDialog.DialogType.YES_CANCEL,
                    object : AppDialog.OnDialogCreated {

                        override fun onCancel() {
                        }

                        override fun onOk() {
                            mLoadingDialog = LoadingDialog.instance
                            mLoadingDialog!!.show(childFragmentManager, null)

                            mPresenter.markAnswerAsResolved(mForumItem.id)
                        }
                    })

                dialog.show(parentFragmentManager, null)
            }

            R.id.forum_options_edit_btn -> {
                val dialog : io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog

                if (mForumItem.isAnswer()) {
                    dialog = ForumReplyDialog()
                    dialog.setOnReplySavedListener(mCallback!!, this)
                } else {
                    dialog = ForumQuestionDialog()
                    dialog.setCallback(mCallback!!, this)
                }

                val bundle = Bundle()
                bundle.putParcelable(io.qurani.android.manager.App.ITEM, mForumItem)

                dialog.arguments = bundle
                dialog.show(parentFragmentManager, null)
            }

            R.id.forum_options_cancel_btn -> {
                dismiss()
            }
        }
    }

    fun setOnSuccessListener(callback: ItemCallback<Any>) {
        mCallback = callback
    }

    fun showResult(response: io.qurani.android.model.BaseResponse) {
        mLoadingDialog?.dismiss()
        ToastMaker.show(requireContext(), response)

        if (response.isSuccessful) {
            mCallback?.onItem(Any())
            dismiss()
        }
    }
}