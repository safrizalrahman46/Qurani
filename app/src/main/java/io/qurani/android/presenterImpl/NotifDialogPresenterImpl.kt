package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.NotifDialog
import retrofit2.Call
import retrofit2.Response

class NotifDialogPresenterImpl(private val dialog: NotifDialog) : Presenter.NotifDialogPresenter {

    override fun setStatusToSeen(notifId: Int) {
        ApiService.apiClient!!.setNotifStatusToSeen(notifId)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        setStatusToSeen(notifId)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null && response.body()!!.isSuccessful) {
                        dialog.onNotifSatusChange()
                    }
                }
            })
    }
}