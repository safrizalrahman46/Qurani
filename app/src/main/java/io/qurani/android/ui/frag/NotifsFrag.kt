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
import io.qurani.android.manager.adapter.CommonRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.model.Notif
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.NotifPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.NotifDialog

class NotifsFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(),
    io.qurani.android.manager.listener.OnItemClickListener, ItemCallback<Int>, EmptyState, MainActivity.OnRefreshListener {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.NotifPresenter
    private lateinit var mNotifs: List<Notif>


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

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.notifications)

       initData()
    }

    private fun initData() {
        if (!io.qurani.android.manager.App.isLoggedIn()) {
            mBinding.rvProgressBar.visibility = View.GONE
            showLoginState()
            return
        }

        mPresenter = NotifPresenterImpl(this)
        mPresenter.getNotifs()
    }

    fun onNotifsReceived(data: Data<Count<Notif>>) {
        mNotifs = data.data!!.items
        mBinding.rvProgressBar.visibility = View.GONE
        if (mNotifs.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = CommonRvAdapter(mNotifs)
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
        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.NOTIF, mNotifs[position])

        val dialog = NotifDialog()
        dialog.setOnStatusChangeListener(this, position)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onItem(position: Int, vararg args: Any) {
        val viewHolder =
            mBinding.rv.findViewHolderForAdapterPosition(position) as CommonRvAdapter.ViewHolder

        viewHolder.hideSatusCircle()
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_notification, R.string.no_notifications, R.string.no_notifications_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        initData()
    }
}