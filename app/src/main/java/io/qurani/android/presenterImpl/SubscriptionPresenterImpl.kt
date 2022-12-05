package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.Subscription
import io.qurani.android.model.SubscriptionItem
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SubscriptionFrag
import retrofit2.Call
import retrofit2.Response

class SubscriptionPresenterImpl(private val frag: SubscriptionFrag) :
    Presenter.SubscriptionPresenter {

    override fun getSubscriptions() {
        val subscriptionsReq = ApiService.apiClient!!.getSubscriptions()
        frag.addNetworkRequest(subscriptionsReq)
        subscriptionsReq.enqueue(object : CustomCallback<Data<Subscription>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getSubscriptions()
                }
            }

            override fun onResponse(
                call: Call<Data<Subscription>>,
                response: Response<Data<Subscription>>
            ) {
                if (response.body() != null) {
                    frag.onSubscriptionsReceived(response.body()!!.data!!)
                }
            }

        })
    }

    override fun checkoutSubscription(subscriptionItem: SubscriptionItem) {
        val checkoutSubscriptionReq = ApiService.apiClient!!.checkoutSubscription(subscriptionItem)
        frag.addNetworkRequest(checkoutSubscriptionReq)
        checkoutSubscriptionReq.enqueue(object :
            CustomCallback<Data<io.qurani.android.model.Response>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    checkoutSubscription(subscriptionItem)
                }
            }

            override fun onResponse(
                call: Call<Data<io.qurani.android.model.Response>>,
                response: Response<Data<io.qurani.android.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onCheckout(response.body()!!)
                }
            }

            override fun onFailure(
                call: Call<Data<io.qurani.android.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        }

        )
    }
}