package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.PayoutRequestDialog
import retrofit2.Call
import retrofit2.Response

class PayoutRequestPresenterImpl(private val dialog: PayoutRequestDialog) :
    Presenter.PayoutRequestPresenter {

    override fun requestPayout() {
        ApiService.apiClient!!.requestPayout(Any()).enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    requestPayout()
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onPayoutSaved(response.body()!!)
                }
            }

        })
    }
}