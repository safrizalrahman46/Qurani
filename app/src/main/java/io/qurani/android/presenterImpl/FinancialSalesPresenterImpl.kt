package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.SalesRes
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.FinancialSalesFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSalesPresenterImpl(private val frag: FinancialSalesFrag) :
    Presenter.FinancialSalesPresenter {

    override fun getSales() {
        val salesReq = ApiService.apiClient!!.getSales()
        frag.addNetworkRequest(salesReq)
        salesReq.enqueue(object : CustomCallback<Data<SalesRes>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getSales()
                }
            }

            override fun onResponse(
                call: Call<Data<SalesRes>>,
                response: Response<Data<SalesRes>>
            ) {
                if (response.body() != null) {
                    frag.onSalesReceived(response.body()!!.data!!)
                }
            }

        })
    }
}