package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.MeetingDetailsMoreDialog
import retrofit2.Call
import retrofit2.Response

class MeetingDetailsMorePresenterImpl(private val dialog: MeetingDetailsMoreDialog) :
    Presenter.MeetingDetailsMorePresenter {

    override fun finishMeeting(meetingId: Int) {
        ApiService.apiClient!!.finishMeeting(meetingId, Any())
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        finishMeeting(meetingId)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingFinished(response.body()!!)
                    }
                }
            })
    }
}