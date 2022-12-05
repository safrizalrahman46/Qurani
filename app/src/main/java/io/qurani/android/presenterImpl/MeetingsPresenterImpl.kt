package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.Meetings
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.MeetingsTabFrag
import retrofit2.Call
import retrofit2.Response

class MeetingsPresenterImpl(private val frag: MeetingsTabFrag) : Presenter.MeetingsPresenter {

    override fun getMeetings() {
        val meetingsReq = ApiService.apiClient!!.getMeetings()
        frag.addNetworkRequest(meetingsReq)
        meetingsReq.enqueue(object : CustomCallback<Data<Meetings>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getMeetings()
                }
            }

            override fun onResponse(
                call: Call<Data<Meetings>>,
                response: Response<Data<Meetings>>
            ) {
                if (response.body() != null) {
                    frag.onMeetingsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}