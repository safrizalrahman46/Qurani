package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.PayoutRes
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.FinancialPayoutFrag
import retrofit2.Call
import retrofit2.Response

class FinancialPayoutPresenterImpl(private val frag: FinancialPayoutFrag) :
    Presenter.FinancialPayoutPresenter {

    override fun getPayouts() {
        val payoutsReq = ApiService.apiClient!!.getPayouts()
        frag.addNetworkRequest(payoutsReq)
        payoutsReq.enqueue(object :CustomCallback<Data<PayoutRes>>{
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getPayouts()
                }
            }

            override fun onResponse(
                call: Call<Data<PayoutRes>>,
                response: Response<Data<PayoutRes>>
            ) {
                if (response.body() != null) {
                    frag.onPayoutsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}