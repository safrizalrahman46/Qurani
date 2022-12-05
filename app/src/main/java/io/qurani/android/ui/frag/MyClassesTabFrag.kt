package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.TabBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class MyClassesTabFrag : Fragment(), EmptyState, MainActivity.OnRefreshListener {

    private lateinit var mBinding: TabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.classes)

        initData()
    }

    private fun initData() {
        if (!io.qurani.android.manager.App.isLoggedIn()) {
            showLoginState()
            return
        }

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val purchasedFrag = MyClassesFrag()
        var bundle = Bundle()
        bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, MyClassesFrag.Type.PURCHASED)

        purchasedFrag.arguments = bundle

        val adapter = io.qurani.android.manager.adapter.ViewPagerAdapter(childFragmentManager)
        adapter.add(purchasedFrag, getString(R.string.purchased))

        if (io.qurani.android.manager.App.loggedInUser!!.isInstructor() || io.qurani.android.manager.App.loggedInUser!!.isOrganizaton()) {

            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, MyClassesFrag.Type.MY_CLASSES)

            val myClassesFrag = MyClassesFrag()
            myClassesFrag.arguments = bundle

            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, MyClassesFrag.Type.ORGANIZATION)

            val organizationFrag = MyClassesFrag()
            organizationFrag.arguments = bundle

            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, MyClassesFrag.Type.INVITED)

            val invitedFrag = MyClassesFrag()
            invitedFrag.arguments = bundle

            adapter.add(myClassesFrag, getString(R.string.my_classes))
            adapter.add(organizationFrag, getString(R.string.organization))
            adapter.add(invitedFrag, getString(R.string.invited))
        } else {
            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, MyClassesFrag.Type.ORGANIZATION)

            val organizationFrag = MyClassesFrag()
            organizationFrag.arguments = bundle
            adapter.add(organizationFrag, getString(R.string.organization))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.tabEmptyState
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