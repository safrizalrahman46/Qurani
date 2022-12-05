package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ReserveTimeMeeting
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.FinalizeReserveMeetingDialog
import retrofit2.Call
import retrofit2.Response

class FinalizeReserveMeetingPresenterImpl(private val dialog: FinalizeReserveMeetingDialog) :
    Presenter.FinalizeReserveMeetingPresenter {

    override fun reserveMeeting(reserveMeeting: ReserveTimeMeeting) {
        ApiService.apiClient!!.reserveMeeting(reserveMeeting)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        reserveMeeting(reserveMeeting)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingReserved(response.body()!!)
                    } else {
                        dialog.onFailed()
                    }
                }

                override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onFailed()
                }
            })
    }
}