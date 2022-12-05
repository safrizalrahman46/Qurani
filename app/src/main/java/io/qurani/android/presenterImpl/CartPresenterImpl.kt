package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.CartFrag
import retrofit2.Call
import retrofit2.Response

class CartPresenterImpl(private val frag: CartFrag) : Presenter.CartPresenter {

    override fun getCart() {
        val cartReq = ApiService.apiClient!!.getCart()
        frag.addNetworkRequest(cartReq)
        cartReq.enqueue(object : CustomCallback<Data<Data<Cart?>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getCart()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<Cart?>>>,
                response: Response<Data<Data<Cart?>>>
            ) {
                if (response.body() != null) {
                    frag.onCartReceived(response.body()!!.data!!.data)
                }
            }

            override fun onFailure(call: Call<Data<Data<Cart?>>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }

    override fun removeFromCart(cartItemId: Int, position: Int) {
        ApiService.apiClient!!.removeFromCart(cartItemId)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        removeFromCart(cartItemId, position)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onCartRemoved(response.body()!!, position)
                    }
                }

            })
    }

    override fun checkout(coupon: Coupon?) {
        val cp = coupon ?: Coupon()
        val checkoutReq = ApiService.apiClient!!.checkout(cp)
        frag.addNetworkRequest(checkoutReq)
        checkoutReq.enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    checkout(coupon)
                }
            }

            override fun onResponse(call: Call<Data<io.qurani.android.model.Response>>, response: Response<Data<io.qurani.android.model.Response>>) {
                if (response.body() != null) {
                    frag.onCheckout(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Data<io.qurani.android.model.Response>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }
}