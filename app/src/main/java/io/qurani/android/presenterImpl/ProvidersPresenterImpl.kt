package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.UsersOrganizationsFrag
import retrofit2.Call
import retrofit2.Response

class ProvidersPresenterImpl(private val frag: UsersOrganizationsFrag) :
    Presenter.ProvidersPresenter {

    override fun getProviders(
        type: UsersOrganizationsFrag.ProviderType,
        map: List<KeyValuePair>?
    ) {
        var url = "providers/"

        url += when (type) {
            UsersOrganizationsFrag.ProviderType.CONSULTANTS -> {
                "consultations"
            }
            UsersOrganizationsFrag.ProviderType.ORGANIZATIONS -> {
                "organizations"
            }
            UsersOrganizationsFrag.ProviderType.INSTRUCTORS -> {
                "instructors"
            }
        }

        if (map != null && map.isNotEmpty()) {
            url += "?"
            for ((index, item) in map.withIndex()) {
                url += "${item.key}=${item.value}"
                if (index != map.size - 1) {
                    url += "&"
                }
            }
        }

        val providersReq = ApiService.apiClient!!.getProviders(url)
        frag.addNetworkRequest(providersReq)
        providersReq.enqueue(object : CustomCallback<Data<Count<User>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getProviders(type, map)
                }
            }

            override fun onResponse(
                call: Call<Data<Count<User>>>,
                response: Response<Data<Count<User>>>
            ) {
                if (response.body() != null) {
                    frag.onProvidersReceived(response.body()!!.data!!)
                }
            }

        })
    }
}