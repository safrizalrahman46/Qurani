package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.qurani.android.R
import io.qurani.android.databinding.FragAssignmentConversationBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.AssignmentConversationPresenterImpl
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.widget.*
import com.xwray.groupie.GroupieAdapter


class AssignmentConversationFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), View.OnClickListener,
    ConversationViews.FileSize {

    private lateinit var mPresenter: Presenter.AssignmentConversationPresenter
    private lateinit var mCommonPresenter: Presenter.CommonApiPresenter
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mConversations: MutableList<Conversation>
    private lateinit var mBinding: FragAssignmentConversationBinding
    private lateinit var mAssignment: Assignment
    private var mIsInstructorType = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragAssignmentConversationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = AssignmentConversationPresenterImpl(this)
        mCommonPresenter = CommonApiPresenterImpl.getInstance()
        mAssignment = requireArguments().getParcelable(io.qurani.android.manager.App.ITEM)!!
        mIsInstructorType = requireArguments().getBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE)

        initUI()
    }

    private fun initUI() {
        getConversations()
        initToolbar()
        initStatus()
        if (mIsInstructorType) {
            iniInstructor()
        } else {
            initStudent()
        }
        mBinding.assignmentConversationPrimaryBtn.setOnClickListener(this)
    }

    private fun getConversations() {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        if (mIsInstructorType && mAssignment.student != null) {
            mPresenter.getConversations(mAssignment, mAssignment.student!!.id)
        } else {
            mPresenter.getConversations(mAssignment, null)
        }
    }

    private fun initStudent() {
        initStatus()
        mBinding.assignmentConversationPrimaryBtn.setText(R.string.submit_assignment)
    }

    private fun iniInstructor() {
        mBinding.assignmentConversationHeaderContainer.visibility = View.VISIBLE
        mBinding.assignmentConversationHeaderTitleTv.text =
            mAssignment.deadlineDueTimeInDays.toString()

        mBinding.assignmentConversationReplyBtn.setOnClickListener(this)
        mBinding.assignmentConversationReplyBtn.visibility = View.VISIBLE
        mBinding.assignmentConversationPrimaryBtn.setText(R.string.rate_assignment)
        if (mAssignment.grade != null) {
            closeAssignment()
        }
    }

    private fun initStatus() {
        if (mAssignment.isPassed()) {
            mBinding.assignmentConversationStatusImg.setBackgroundResource(R.drawable.circle_accent)
            mBinding.assignmentConversationStatusImg.setImageResource(R.drawable.ic_done_rect_white)
            mBinding.assignmentConversationStatusTitleTv.setText(R.string.assignment_passed)
            mBinding.assignmentConversationStatusDescTv.setText(R.string.cannot_send_any_file)
            mBinding.assignmentConversationStatusContainer.visibility = View.VISIBLE
            mBinding.assignmentConversationBtnsContainer.visibility = View.GONE
        } else if (mAssignment.isClosed()) {
            closeAssignment()
        }
    }

    private fun initToolbar() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(
            toolbarOptions,
            getString(R.string.assignment_history)
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.assignment_conversation_primary_btn -> {
                if (mIsInstructorType) {
                    showRateDialog()
                } else {
                    showReplyDialog()
                }
            }

            R.id.assignment_conversation_reply_btn -> {
                showReplyDialog()
            }
        }
    }

    private fun showReplyDialog() {
        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.ITEM, mAssignment)
        bundle.putBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE, mIsInstructorType)

        val dialog = AssignmentConversationDialog()
        dialog.arguments = bundle
        dialog.setCallback(object : ItemCallback<Any> {
            override fun onItem(item: Any, vararg args: Any) {
                getConversations()
            }
        })
        dialog.show(childFragmentManager, null)
    }

    private fun showRateDialog() {
        val bundle = Bundle()
        bundle.putInt(io.qurani.android.manager.App.ID, mAssignment.id)

        val dialog = RateAssignmentDialog()
        dialog.arguments = bundle
        dialog.setOnGradeSaved(object : ItemCallback<Any> {
            override fun onItem(item: Any, vararg args: Any) {
                closeAssignment()
            }
        })
        dialog.show(childFragmentManager, null)
    }

    private fun closeAssignment() {
        mBinding.assignmentConversationStatusImg.setBackgroundResource(R.drawable.circle_red)
        mBinding.assignmentConversationStatusImg.setImageResource(R.drawable.ic_close_rect_white)
        mBinding.assignmentConversationStatusTitleTv.setText(R.string.assignment_closed)
        mBinding.assignmentConversationStatusDescTv.setText(R.string.cannot_send_any_file)
        mBinding.assignmentConversationStatusContainer.visibility = View.VISIBLE
        mBinding.assignmentConversationBtnsContainer.visibility = View.GONE
    }

    fun addConversations(conversations: MutableList<Conversation>) {
        mConversations = conversations
        val adapter = GroupieAdapter()

        for (conversation in conversations) {
            if (conversation.supporter != null || conversation.sender!!.id != io.qurani.android.manager.App.loggedInUser!!.id) {
                if (conversation.supporter == null) {
                    conversation.supporter = conversation.sender
                }

                adapter.add(ConversationViews.SystemUserItem(conversation))
                if (conversation.filePath != null) {
                    val attachmentItem = ConversationViews.AttachmentItem(
                        this,
                        conversation, true
                    )
                    adapter.add(attachmentItem)
                }

            } else {
                adapter.add(ConversationViews.UserItem(conversation))
                if (conversation.filePath != null) {
                    adapter.add(
                        ConversationViews.AttachmentItem(
                            this,
                            conversation, false
                        )
                    )
                }
            }
        }

        mBinding.assignmentConversationRv.adapter = adapter
    }

    override fun getFileSize(conversation: Conversation, callback: ItemCallback<Long>) {
        mCommonPresenter.getFileSize(conversation.filePath!!, callback)
    }

    fun onConversationsReceived(conversations: List<Conversation>) {
        mLoadingDialog.dismiss()
        addConversations(conversations.toMutableList())
    }
}