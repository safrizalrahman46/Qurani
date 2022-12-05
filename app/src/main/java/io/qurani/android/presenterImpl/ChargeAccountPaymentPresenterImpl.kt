package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.PaymentRequest
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.ChargeAccountPaymentFrag
import retrofit2.Call
import retrofit2.Response

class ChargeAccountPaymentPresenterImpl(private val frag: ChargeAccountPaymentFrag) :
    Presenter.ChargeAccountPaymentPresenter {

    override fun requestPayment(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.requestPayment(paymentRequest)
    }

    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
    }

    override fun requestPaymentFromCharge(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.payWithCredit(paymentRequest).enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse>{
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    requestPaymentFromCharge(paymentRequest)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    frag.onPaymentWithCharge(response.body()!!)
                }
            }

            override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }

}