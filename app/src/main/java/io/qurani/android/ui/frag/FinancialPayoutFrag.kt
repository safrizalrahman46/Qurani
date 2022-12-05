package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragFinancialPayoutBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.Utils
import io.qurani.android.manager.adapter.CommonRvAdapter
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.PayoutAccount
import io.qurani.android.model.PayoutRes
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.FinancialPayoutPresenterImpl
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.PayoutRequestDialog

class FinancialPayoutFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), View.OnClickListener, EmptyState {

    private lateinit var mBinding: FragFinancialPayoutBinding
    private lateinit var mPresenter: Presenter.FinancialPayoutPresenter

    private lateinit var mPayoutAccount: PayoutAccount

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragFinancialPayoutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = FinancialPayoutPresenterImpl(this)
        mPresenter.getPayouts()
    }

    fun onPayoutsReceived(res: PayoutRes) {
        mPayoutAccount = res.payoutAccount
        mBinding.financialPayoutRvProgressBar.visibility = View.GONE
        if (res.payouts.isNotEmpty()) {
            mBinding.financialPayoutRv.adapter = CommonRvAdapter(res.payouts)
        } else {
            showEmptyState()
        }

        mBinding.financialPayoutAmountTv.text =
            Utils.formatPrice(requireContext(), res.payoutAccount.amonut, false)
        mBinding.financialPayoutRequestBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.PAYOUT_ACCOUT, mPayoutAccount)

        val dialog = PayoutRequestDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_payout, R.string.no_payout, R.string.no_payout_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialPayoutEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}