package io.qurani.android.ui.frag

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragForumAnswersBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.PermissionManager
import io.qurani.android.manager.adapter.BaseArrayAdapter
import io.qurani.android.manager.adapter.ForumAnswersRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.ForumItem
import io.qurani.android.model.ForumItemAnswer
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.ForumAnswersPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.ForumReplyDialog

class ForumAnswersFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState, View.OnClickListener, ItemCallback<Any> {

    private lateinit var mBinding: FragForumAnswersBinding
    private lateinit var mForumItem: ForumItem
    private lateinit var mPresenter: Presenter.ForumAnswersPresenter
    private lateinit var mStoragePermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mPermissionManager: PermissionManager

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStoragePermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (mPermissionManager.isGranted()
                ) {
                    val adapter = mBinding.forumAnswersReplyRv.adapter as ForumAnswersRvAdapter
                    adapter.downloadFile(requireContext(), adapter.items[0].attachment!!)
                }
            }
        mPermissionManager =
            PermissionManager(requireContext(), PERMISSIONS, mStoragePermissionResultLauncher)
    }

    fun hasPermission(): Boolean {
        return mPermissionManager.isGranted()
    }

    fun requestPermission() {
        return mPermissionManager.request()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragForumAnswersBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mForumItem = requireArguments().getParcelable(io.qurani.android.manager.App.ITEM)!!
        mPresenter = ForumAnswersPresenterImpl(this)
        initUI()
        getAnswers()
    }

    private fun initUI() {
        mBinding.forumAnswersReplyBtn.setOnClickListener(this)
        setToolbar()
    }

    private fun setToolbar() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, mForumItem.title)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.forumAnswersEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_comments, R.string.no_answers, R.string.no_answers_desc)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.ITEM, mForumItem)

        val dialog = ForumReplyDialog()
        dialog.setOnReplySavedListener(this)
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, null)
    }

    fun onAnswersReceived(answers: List<ForumItemAnswer>) {
        mBinding.forumAnswersReplyRvProgrssBar.visibility = View.GONE

        val question = ForumItemAnswer()
        question.title = mForumItem.title
        question.description = mForumItem.description
        question.user = mForumItem.user
        question.createdAt = mForumItem.createdAt
        question.attachment = null

        val answersList = answers.toMutableList()
        answersList.add(0, question)

        if (answers.isEmpty()) {
            showEmptyState()
        } else {
            mBinding.forumAnswersReplyRv.adapter =
                ForumAnswersRvAdapter(answersList, this, this)
        }
    }

    override fun onItem(item: Any, vararg args: Any) {
        getAnswers()
    }

    private fun getAnswers() {
        mBinding.forumAnswersReplyRvProgrssBar.visibility = View.VISIBLE

        val adapter = mBinding.forumAnswersReplyRv.adapter

        if (adapter != null) {
            val baseArrayAdapter = adapter as io.qurani.android.manager.adapter.BaseArrayAdapter<*, *>
            val size = baseArrayAdapter.items.size
            baseArrayAdapter.items.clear()
            baseArrayAdapter.notifyItemRangeRemoved(0, size)
        }

        mPresenter.getForumQuestionAnswers(mForumItem.id)
    }
}