package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragFinancialSummaryBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.Utils
import io.qurani.android.manager.adapter.CommonRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.model.view.PaymentRedirection
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import io.qurani.android.presenterImpl.FinancialSummaryPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.PaymentStatusActivity
import io.qurani.android.ui.frag.abs.EmptyState

class FinancialSummaryFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), View.OnClickListener,
    ItemCallback<QuickInfo>, EmptyState {

    private lateinit var mBinding: FragFinancialSummaryBinding
    private lateinit var mPresenter: Presenter.FinancialSummaryPresenter
    private lateinit var mCommonPresenter: Presenter.CommonApiPresenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragFinancialSummaryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.financialSummaryEmptyState.root.visibility = View.INVISIBLE

        mCommonPresenter = CommonApiPresenterImpl.getInstance()
        mCommonPresenter.getQuickInfo(this)

        mPresenter = FinancialSummaryPresenterImpl(this)
        mPresenter.getSummary()

        mBinding.financialSummaryChargeBtn.setOnClickListener(this)
    }

    fun onSummariesReceived(summaries: List<FinancialSummary>) {
        mBinding.financialSummaryRvProgressBar.visibility = View.INVISIBLE
        if (summaries.isNotEmpty()) {
            mBinding.financialSummaryRv.adapter = CommonRvAdapter(summaries)
        } else {
            showEmptyState()
        }
    }

    override fun onClick(v: View?) {
        val redirection = PaymentRedirection()
        redirection.isNavDrawer = true
        redirection.position = MainActivity.SlideMenuItem.FINANCIAL.value()
        redirection.buttonTitle = getString(R.string.financial)

        PaymentStatusActivity.paymentRedirection = redirection

        mPresenter.chargeAccount(PaymentRequest())
//        val dialog = ChargeDialog()
//        dialog.show(childFragmentManager, null)


//        val bundle = Bundle()
//        bundle.putParcelable(App.REDIRECTION, redirection)
//
//        val chargeAccountFrag = ChargeAccountPaymentFrag()
//        chargeAccountFrag.arguments = bundle
//
//        (activity as MainActivity).transact(chargeAccountFrag)
    }

    override fun onItem(quickInfo: QuickInfo, vararg args: Any) {
        if (context == null) return

        io.qurani.android.manager.App.quickInfo = quickInfo
        mBinding.financialSummaryCreditTv.text =
            Utils.formatPrice(requireContext(), quickInfo.balance, false)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_payout, R.string.no_balance, R.string.no_balance_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialSummaryEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onCheckout(data: Data<io.qurani.android.model.Response>) {
        if (data.isSuccessful && !data.data!!.link.isNullOrEmpty()) {
            Utils.openLink(requireContext(), data.data!!.link)
        } else {
            if (context == null) return
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}