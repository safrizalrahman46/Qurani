package io.qurani.android.presenterImpl

import android.util.Log
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.model.Notif
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.NotifsFrag
import retrofit2.Call
import retrofit2.Response

class NotifPresenterImpl(private val frag: NotifsFrag) : Presenter.NotifPresenter {

    override fun getNotifs() {
        val notifsReq = ApiService.apiClient!!.getNotifs()
        frag.addNetworkRequest(notifsReq)
        notifsReq.enqueue(object :CustomCallback<Data<Count<Notif>>>{
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getNotifs()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Notif>>>,
                response: Response<Data<Count<Notif>>>
            ) {
                if (response.body() != null) {
                    frag.onNotifsReceived(response.body()!!)
                }
            }
        })
    }
}