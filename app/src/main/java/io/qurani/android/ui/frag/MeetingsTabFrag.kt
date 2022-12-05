package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.qurani.android.R
import io.qurani.android.databinding.TabBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Meeting
import io.qurani.android.model.Meetings
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.MeetingsPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.widget.LoadingDialog
import java.util.ArrayList

class MeetingsTabFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment() {

    private lateinit var mBinding: TabBinding
    private lateinit var mPresenter: Presenter.MeetingsPresenter
    private lateinit var mLoadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.meetings)

        mPresenter = MeetingsPresenterImpl(this)
        mPresenter.getMeetings()
    }

    fun onMeetingsReceived(meetings: Meetings) {
        val reservedItems = meetings.reservations

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val reservedFrag = MeetingsFrag()

        var bundle = Bundle()
        bundle.putParcelableArrayList(io.qurani.android.manager.App.MEETINGS, reservedItems.items as ArrayList<Meeting>)
        reservedFrag.arguments = bundle

        val adapter = io.qurani.android.manager.adapter.ViewPagerAdapter(childFragmentManager)
        adapter.add(reservedFrag, getString(R.string.reserved))

        if (io.qurani.android.manager.App.loggedInUser!!.isInstructor() || io.qurani.android.manager.App.loggedInUser!!.isOrganizaton()) {
            val requestsFrag = MeetingsFrag()
            val requestItems = meetings.requests
            bundle = Bundle()
            bundle.putParcelableArrayList(io.qurani.android.manager.App.MEETINGS, requestItems.items as ArrayList<Meeting>)
            requestsFrag.arguments = bundle
            adapter.add(requestsFrag, getString(R.string.requests))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        mLoadingDialog.dismiss()
    }

}