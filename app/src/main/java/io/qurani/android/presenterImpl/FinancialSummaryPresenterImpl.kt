package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.model.FinancialSummary
import io.qurani.android.model.PaymentRequest
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.FinancialSummaryFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSummaryPresenterImpl(private val frag: FinancialSummaryFrag) :
    Presenter.FinancialSummaryPresenter {

    override fun getSummary() {
        val financialSummaryReq = ApiService.apiClient!!.getFinancialSummary()
        frag.addNetworkRequest(financialSummaryReq)
        financialSummaryReq.enqueue(object : CustomCallback<Data<Count<FinancialSummary>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getSummary()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<FinancialSummary>>>,
                response: Response<Data<Count<FinancialSummary>>>
            ) {
                if (response.body() != null) {
                    frag.onSummariesReceived(response.body()!!.data!!.items)
                }
            }
        })
    }

    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<io.qurani.android.model.Response>>,
                    response: retrofit2.Response<Data<io.qurani.android.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onCheckout(response.body()!!)
                    }
                }

            })
    }

}