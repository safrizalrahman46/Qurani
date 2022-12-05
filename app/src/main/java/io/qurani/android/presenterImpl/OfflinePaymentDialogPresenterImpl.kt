package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.OfflinePayment
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.OfflinePaymentDialog
import retrofit2.Call
import retrofit2.Response

class OfflinePaymentDialogPresenterImpl(private val dialog: OfflinePaymentDialog) :
    Presenter.OfflinePaymentDialogPresenter {

    override fun addOfflinePayment(offlinePayment: OfflinePayment) {
        val addOfflinePaymentsReq = ApiService.apiClient!!.addOfflinePayments(offlinePayment)
        dialog.addNetworkRequest(addOfflinePaymentsReq)
        addOfflinePaymentsReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    addOfflinePayment(offlinePayment)
                }
            }

            override fun onResponse(
                call: Call<io.qurani.android.model.BaseResponse>,
                response: Response<io.qurani.android.model.BaseResponse>
            ) {
                if (response.body() != null) {
                    dialog.onOfflinePaymentSaved(response.body()!!)
                }
            }

        })
    }
}