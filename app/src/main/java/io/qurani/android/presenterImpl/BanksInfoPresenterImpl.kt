package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.model.SystemBankAccount
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.BanksInfoFrag
import retrofit2.Call
import retrofit2.Response

class BanksInfoPresenterImpl(private val frag: BanksInfoFrag) : Presenter.BanksInfoPresenter {

    override fun getBanksInfo() {
        val bankInfosReq = ApiService.apiClient!!.getBankInfos()
        frag.addNetworkRequest(bankInfosReq)
        bankInfosReq.enqueue(object : CustomCallback<Data<Count<SystemBankAccount>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getBanksInfo()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<SystemBankAccount>>>,
                    response: Response<Data<Count<SystemBankAccount>>>
                ) {
                    if (response.body() != null) {
                        frag.onInfosReceived(response.body()!!.data!!.items)
                    }
                }

            })
    }
}