package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.NoticeRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Notice
import io.qurani.android.presenterImpl.NoticesPresenterImpl
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.NoticeDialog

class NoticesFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState,
    io.qurani.android.manager.listener.OnItemClickListener {

    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val courseId = requireArguments().getInt(io.qurani.android.manager.App.ID)

        val presenter = NoticesPresenterImpl(this)
        presenter.getNotices(courseId)
    }

    fun onNoticesReceived(notices: List<Notice>) {
        mBinding.rvProgressBar.visibility = View.GONE
        if (notices.isEmpty()) {
            showEmptyState()
        } else {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = NoticeRvAdapter(notices)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_blog, R.string.no_notices, R.string.no_notices_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val notice = (mBinding.rv.adapter as NoticeRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.ITEM, notice)

        val dialog = NoticeDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }
}