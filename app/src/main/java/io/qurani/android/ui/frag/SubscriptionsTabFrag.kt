package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.qurani.android.R
import io.qurani.android.databinding.TabBinding
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.ui.MainActivity

class SubscriptionsTabFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment() {

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
        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.subscription)

        val adapter = io.qurani.android.manager.adapter.ViewPagerAdapter(childFragmentManager)
        adapter.add(SubscriptionFrag(), getString(R.string.subscription))
        adapter.add(SaasPackageFrag(), getString(R.string.saas_package))

        mBinding.viewPager.adapter = adapter
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)
    }
}