package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ReserveMeetingDialog
import retrofit2.Call
import retrofit2.Response

class ReserveMeetingDialogPresenterImpl(private val dialog: ReserveMeetingDialog) :
    Presenter.ReserveMeetingDialogPresenter {

    override fun getAvailableMeetingTimes(userId: Int, date: String) {
        val availableMeetingTimesReq = ApiService.apiClient!!.getAvailableMeetingTimes(userId, date)
        dialog.addNetworkRequest(availableMeetingTimesReq)
        availableMeetingTimesReq.enqueue(object : CustomCallback<Data<Count<Timing>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getAvailableMeetingTimes(userId, date)
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Timing>>>,
                response: Response<Data<Count<Timing>>>
            ) {
                if (response.body() != null) {
                    dialog.onTimingsReceived(response.body()!!.data!!.items)
                }
            }
        })
    }
}