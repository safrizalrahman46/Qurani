package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import io.qurani.android.R
import io.qurani.android.databinding.FragTicketConversationBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Ticket
import io.qurani.android.model.Conversation
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import io.qurani.android.presenterImpl.TicketConversationPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.widget.*
import com.xwray.groupie.GroupieAdapter


class TicketConversationFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), View.OnClickListener,
    ConversationViews.FileSize {

    private lateinit var mPresenter: Presenter.TicketConversationPresenter
    private lateinit var mTicket: Ticket
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mConversations: MutableList<Conversation>
    private lateinit var mBinding: FragTicketConversationBinding
    private lateinit var mCommonPresenter: Presenter.CommonApiPresenter
    private var mPosition = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragTicketConversationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bgGray))

        mPresenter = TicketConversationPresenterImpl(this)
        mCommonPresenter = CommonApiPresenterImpl.getInstance()
        mTicket = requireArguments().getParcelable(io.qurani.android.manager.App.TICKET)!!
        mPosition = requireArguments().getInt(io.qurani.android.manager.App.POSITION)
        addConversations(mTicket.conversations.toMutableList())

        if (mTicket.course != null) {
            mBinding.ticketConversationHeaderInfoDescTv.text = mTicket.course!!.title
            mBinding.ticketConversationHeaderContainer.visibility = View.VISIBLE
            mBinding.ticketConversationHeaderContainer.setOnClickListener(this)
        }

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, mTicket.title)

        if (mTicket.status == Ticket.Status.CLOSE.value()) {
            mBinding.ticketConversationCancelBtn.visibility = View.GONE
        }

        mBinding.ticketConversationReplyBtn.setOnClickListener(this)
        mBinding.ticketConversationCancelBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ticketConversationCancelBtn -> {
                val dialog = AppDialog()
                val bundle = Bundle()
                bundle.putString(io.qurani.android.manager.App.TITLE, getString(R.string.close))
                bundle.putString(io.qurani.android.manager.App.TEXT, getString(R.string.close_ticket_desc))
                dialog.arguments = bundle
                dialog.setOnDialogBtnsClickedListener(AppDialog.DialogType.YES_CANCEL,
                    object : AppDialog.OnDialogCreated {

                        override fun onCancel() {
                        }

                        override fun onOk() {
                            mLoadingDialog = LoadingDialog.instance
                            mLoadingDialog.show(childFragmentManager, null)

                            mPresenter.closeTicket(mTicket.id)
                        }

                    })
                dialog.show(childFragmentManager, null)
            }

            R.id.ticketConversationReplyBtn -> {
                val bundle = Bundle()
                bundle.putInt(io.qurani.android.manager.App.ID, mTicket.id)
                bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, NewTicketDialog.Type.PLATFORM_SUPPORT)

                val dialog = NewTicketDialog()
                dialog.arguments = bundle
                dialog.setOnTicketChatSavedListener(object : ItemCallback<Conversation> {
                    override fun onItem(conversation: Conversation, vararg args: Any) {
                        conversation.createdAt = System.currentTimeMillis() / 1000

                        if (!mBinding.ticketConversationCancelBtn.isVisible)
                            mBinding.ticketConversationCancelBtn.visibility = View.VISIBLE

                        val adapter = mBinding.ticketConversationRv.adapter as GroupieAdapter
                        adapter.add(ConversationViews.UserItem(conversation))
                        if (conversation.attachment != null)
                            adapter.add(
                                ConversationViews.AttachmentItem(
                                    this@TicketConversationFrag,
                                    conversation,
                                    false,
                                )
                            )
                        mConversations.add(conversation)
                    }
                })
                dialog.show(childFragmentManager, null)
            }

            R.id.ticketConversationHeaderContainer -> {
                val bundle = Bundle()
                bundle.putParcelable(io.qurani.android.manager.App.COURSE, mTicket.course)

                val frag = CourseDetailsFrag()
                frag.arguments = bundle
                (activity as MainActivity).transact(frag)
            }
        }
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
                if (conversation.attachment != null) {
                    val attachmentItem = ConversationViews.AttachmentItem(
                        this,
                        conversation, true
                    )
                    adapter.add(attachmentItem)
                }

            } else {
                adapter.add(ConversationViews.UserItem(conversation))
                if (conversation.attachment != null)
                    adapter.add(
                        ConversationViews.AttachmentItem(
                            this,
                            conversation, false
                        )
                    )
            }
        }

        mBinding.ticketConversationRv.adapter = adapter
    }

    fun onTicketClosed(response: io.qurani.android.model.BaseResponse) {
        if (context == null) return

        mLoadingDialog.dismiss()

        val title: String
        val type: ToastMaker.Type

        if (response.isSuccessful) {
            title = getString(R.string.success)
            type = ToastMaker.Type.SUCCESS
        } else {
            title = getString(R.string.error)
            type = ToastMaker.Type.ERROR
        }

        ToastMaker.show(requireContext(), title, response.message, type)

        if (response.isSuccessful) {
            parentFragment
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

    override fun getFileSize(conversation: Conversation, callback: ItemCallback<Long>) {
        mCommonPresenter.getFileSize(conversation.attachment!!, callback)
    }
}