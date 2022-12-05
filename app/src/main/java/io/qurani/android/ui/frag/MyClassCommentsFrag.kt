package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ClassCommentRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.model.Comments
import io.qurani.android.presenterImpl.CommentsPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class MyClassCommentsFrag : Fragment(), io.qurani.android.manager.listener.OnItemClickListener, ItemCallback<Comments>, EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mCommentsFrag: CommentsFrag

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        val presenter = CommentsPresenterImpl()
        presenter.getComments(this)
    }

    override fun onItem(item: Comments, vararg args: Any) {
        if (context == null) return

        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (item.classComments.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = ClassCommentRvAdapter(item.classComments)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        } else {
            showEmptyState()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val comment = (mBinding.rv.adapter as ClassCommentRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.COMMENT, comment)
        bundle.putBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE, true)

        val frag = CommentDetailsFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_comments,
            R.string.no_comments,
            R.string.no_comments_instructor_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}