package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.PaymentRequest
import io.qurani.android.model.Response
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ChargeDialog
import retrofit2.Call

class ChargeAccountPresenterImpl(private val dialog: ChargeDialog) :
    Presenter.ChargeAccountPresenter {
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
                        dialog.onCheckout(response.body()!!)
                    }
                }

            })
    }

}