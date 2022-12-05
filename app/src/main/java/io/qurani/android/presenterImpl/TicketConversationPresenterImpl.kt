package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.TicketConversationFrag
import retrofit2.Call
import retrofit2.Response

class TicketConversationPresenterImpl(private val frag: TicketConversationFrag) :
    Presenter.TicketConversationPresenter {

    override fun closeTicket(ticketId: Int) {
        ApiService.apiClient!!.closeTicket(ticketId).enqueue(object :CustomCallback<io.qurani.android.model.BaseResponse>{
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    closeTicket(ticketId)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    frag.onTicketClosed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }
}