package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragSubscriptionBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.Utils
import io.qurani.android.manager.adapter.SubscriptionSliderAdapter
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.model.view.PaymentRedirection
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.SubscriptionPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.PaymentStatusActivity
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.LoadingDialog
import kotlin.math.roundToInt

class SubscriptionFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment() {

    private lateinit var mBinding: FragSubscriptionBinding
    private lateinit var mPresenter: Presenter.SubscriptionPresenter
    private lateinit var mLoadingDialog: LoadingDialog

    private val mPlanEmptyState = object : EmptyState {
        override fun emptyViewBinding(): EmptyStateBinding {
            return mBinding.subscriptionPlanEmptyState
        }

        override fun getVisibleFrag(): Fragment {
            return this@SubscriptionFrag
        }
    }

    private val mUserEmptyState = object : EmptyState {
        override fun emptyViewBinding(): EmptyStateBinding {
            return mBinding.subscriptionUserEmptyState
        }

        override fun getVisibleFrag(): Fragment {
            return this@SubscriptionFrag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSubscriptionBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if (io.qurani.android.manager.App.loggedInUser!!.isUser()) {
            val toolbarOptions = ToolbarOptions()
            toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

            (activity as MainActivity).showToolbar(toolbarOptions, R.string.subscription)
        }

        mPresenter = SubscriptionPresenterImpl(this)
        mPresenter.getSubscriptions()
    }

    fun onSubscriptionsReceived(subscription: Subscription) {
        if (subscription.subscribed) {
            mBinding.subscriptionActivePlanTv.text = subscription.subscribedTitle
            mBinding.subscriptionRemainedDownloadsTv.text =
                subscription.remainedDownloads.toString()
            mBinding.subscriptionRemainedDaysTv.text = subscription.daysRemained.toString()
            mBinding.subscriptionHeaderContainer.visibility = View.VISIBLE
        }

        if (!subscription.subscribed && subscription.subscritionItems.isEmpty()) {
            val params =
                mBinding.subscriptionPlanEmptyState.root.layoutParams as RelativeLayout.LayoutParams
            params.removeRule(RelativeLayout.BELOW)
            params.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.CENTER_IN_PARENT)
            mBinding.subscriptionPlanEmptyState.root.requestLayout()

            mPlanEmptyState.showEmptyState(
                R.drawable.no_subscription,
                getString(R.string.no_subscription),
                getString(R.string.no_subscription_desc)
            )

            mBinding.subscriptionPlanTv.visibility = View.GONE

        } else if (subscription.subscritionItems.isEmpty()) {

            mPlanEmptyState.showEmptyState(
                R.drawable.no_subscription,
                getString(R.string.no_subscription),
                getString(R.string.no_subscription_desc)
            )

            mBinding.subscriptionPlanTv.visibility = View.GONE

        } else if (!subscription.subscribed) {
            val padding = Utils.changeDpToPx(requireContext(), 16f).roundToInt()
            mBinding.subscriptionUserEmptyState.root.setPadding(0, padding, 0, padding)
            mBinding.subscriptionUserEmptyState.root.setBackgroundResource(R.drawable.bordered_view_1dp)
            mBinding.subscriptionUserEmptyState.emptyStateImg.maxHeight =
                Utils.changeDpToPx(requireContext(), 100f).roundToInt()

            val params =
                mBinding.subscriptionPlanTv.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.subscriptionUserEmptyState)
            mBinding.subscriptionPlanTv.requestLayout()

            mUserEmptyState.showEmptyState(
                R.drawable.no_subscription,
                getString(R.string.no_subscription),
                getString(R.string.no_subscription_desc_student)
            )
        }

        mBinding.subscriptionViewPager.adapter =
            SubscriptionSliderAdapter(subscription.subscritionItems, this, subscription.subscribed)
        mBinding.subscriptionIndicator.setViewPager2(mBinding.subscriptionViewPager)
    }

    fun onItemSelected(item: SubscriptionItem) {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        item.subscribeId = item.id

        mPresenter.checkoutSubscription(item)
    }

    fun onCheckout(data: Data<io.qurani.android.model.Response>) {
        mLoadingDialog.dismiss()

        if (data.isSuccessful && !data.data!!.link.isNullOrEmpty()) {
            val redirection = PaymentRedirection()
            redirection.isNavDrawer = true
            redirection.position = MainActivity.SlideMenuItem.SUBSCRIPTION.value()
            redirection.buttonTitle = getString(R.string.my_subscription)

            PaymentStatusActivity.paymentRedirection = redirection

            Utils.openLink(requireContext(), data.data!!.link)
//            val bundle = Bundle()
//            bundle.putParcelable(App.ORDER, data.data)
//

//            bundle.putParcelable(App.REDIRECTION, redirection)
//
//            val paymentFrag = ChargeAccountPaymentFrag()
//            paymentFrag.arguments = bundle
//            (activity as MainActivity).transact(paymentFrag)
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }
}