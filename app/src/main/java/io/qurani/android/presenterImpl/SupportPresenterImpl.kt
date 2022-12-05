package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.Ticket
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SupportFrag
import retrofit2.Call
import retrofit2.Response

class SupportPresenterImpl(private val frag: SupportFrag) : Presenter.SupportPresenter {

    override fun getTickets() {
        val supportsReq = ApiService.apiClient!!.getTickets()
        frag.addNetworkRequest(supportsReq)
        supportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getTickets()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getClassSupport() {
        val classSupportsReq = ApiService.apiClient!!.getClassSupports()
        frag.addNetworkRequest(classSupportsReq)
        classSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getMyClassSupport() {
        val myClassSupportsReq = ApiService.apiClient!!.getMyClassSupports()
        frag.addNetworkRequest(myClassSupportsReq)
        myClassSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getMyClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }
}