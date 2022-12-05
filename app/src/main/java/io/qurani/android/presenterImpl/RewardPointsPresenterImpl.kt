package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.Points
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.RewardPointsFrag
import retrofit2.Call
import retrofit2.Response

class RewardPointsPresenterImpl(private val frag: RewardPointsFrag) :
    Presenter.RewardPointsPresenter {

    override fun getPoints() {
        val pointsReq = ApiService.apiClient!!.getPoints()
        frag.addNetworkRequest(pointsReq)
        pointsReq.enqueue(object : CustomCallback<Data<Points>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getPoints()
                }
            }

            override fun onResponse(call: Call<Data<Points>>, res: Response<Data<Points>>) {
                if (res.body() != null) {
                    frag.onPointsReceived(res.body()!!.data!!)
                }
            }
        })
    }
}