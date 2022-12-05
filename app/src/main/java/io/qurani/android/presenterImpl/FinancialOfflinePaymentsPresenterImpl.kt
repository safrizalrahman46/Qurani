package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.OfflinePayment
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.FinancialOfflinePaymentsFrag
import retrofit2.Call
import retrofit2.Response

class FinancialOfflinePaymentsPresenterImpl(private val frag: FinancialOfflinePaymentsFrag) :
    Presenter.FinancialOfflinePaymentsPresenter {

    override fun getOfflinePayments() {
        val offlinePaymentsReq = ApiService.apiClient!!.getOfflinePayments()
        frag.addNetworkRequest(offlinePaymentsReq)
        offlinePaymentsReq.enqueue(object : CustomCallback<Data<List<OfflinePayment>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getOfflinePayments()
                }
            }

            override fun onResponse(
                call: Call<Data<List<OfflinePayment>>>,
                response: Response<Data<List<OfflinePayment>>>
            ) {
                if (response.body() != null) {
                    frag.onPaymentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}