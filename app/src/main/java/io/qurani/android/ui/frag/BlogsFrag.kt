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
import io.qurani.android.manager.adapter.BlogRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Blog
import io.qurani.android.model.Category
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.BlogPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.BlogCategoriesDialog

class BlogsFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(),
    io.qurani.android.manager.listener.OnItemClickListener, EmptyState,
    ItemCallback<Category> {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.BlogPresenter
    private var mCatId: Int? = null

    private val mSelectedCatClickCallback = object : ItemCallback<Any> {
        override fun onItem(item: Any, vararg args: Any) {
            val dialog = BlogCategoriesDialog()
            dialog.setCallback(this@BlogsFrag)
            dialog.show(childFragmentManager, null)
        }
    }

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
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV
        toolbarOptions.endIcon = ToolbarOptions.Icon.FILTER

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.blogs)
        (activity as MainActivity).setOnFilterButtonClickListener(mSelectedCatClickCallback)

        mPresenter = BlogPresenterImpl(this)

        if (mCatId == null) {
            mPresenter.getBlogs()
        } else {
            mPresenter.getBlogs(mCatId!!)
        }
    }

    fun onBlogsRecevied(data: List<Blog>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE

        if (data.isNotEmpty()) {

            if (mBinding.rv.adapter == null) {
                mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
                mBinding.rv.addOnItemTouchListener(
                    io.qurani.android.manager.listener.ItemClickListener(
                        mBinding.rv,
                        this
                    )
                )
                mBinding.rv.adapter = BlogRvAdapter(data)
            } else {
                val blogAdapter = mBinding.rv.adapter as BlogRvAdapter
                blogAdapter.items.addAll(data)
                blogAdapter.notifyItemRangeInserted(0, blogAdapter.itemCount)
            }

        } else {
            showEmptyState()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val blog = (mBinding.rv.adapter as BlogRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.BLOG, blog)

        val blogDetailsFrag = BlogDetailsFrag()
        blogDetailsFrag.arguments = bundle

        (activity as MainActivity).transact(blogDetailsFrag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }


    fun showEmptyState() {
        showEmptyState(R.drawable.no_blog, R.string.no_blog, R.string.no_blog_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun onItem(item: Category, vararg args: Any) {
        mCatId = item.id

        val blogAdapter = mBinding.rv.adapter as BlogRvAdapter
        val size = blogAdapter.itemCount
        blogAdapter.items.clear()
        blogAdapter.notifyItemRangeRemoved(0, size)

        mBinding.rvProgressBar.visibility = View.VISIBLE

        mPresenter.getBlogs(item.id)
    }
}