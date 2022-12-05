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
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.ui.MainActivity

class SupportTabFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment() {

    private lateinit var mBinding: TabBinding

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
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.support_messages)

        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        initTabs()
    }

    private fun initTabs() {
        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val ticketsFrag = SupportFrag()
        val classesSupportFrag = SupportFrag()

        var bundle = Bundle()
        bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, SupportFrag.Type.TICKETS)

        ticketsFrag.arguments = bundle

        bundle = Bundle()
        bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, SupportFrag.Type.CLASS_SUPPORT)
        classesSupportFrag.arguments = bundle

        val adapter = io.qurani.android.manager.adapter.ViewPagerAdapter(childFragmentManager)
        adapter.add(ticketsFrag, getString(R.string.tickets))
        adapter.add(classesSupportFrag, getString(R.string.classes_support))

        if (io.qurani.android.manager.App.loggedInUser!!.isOrganizaton() || io.qurani.android.manager.App.loggedInUser!!.isInstructor()) {
            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, SupportFrag.Type.MY_CLASS_SUPPORT)
            val myClassesSupportFrag = SupportFrag()
            myClassesSupportFrag.arguments = bundle
            adapter.add(myClassesSupportFrag, getString(R.string.my_classes_support))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}