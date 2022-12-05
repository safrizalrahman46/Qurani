package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Coupon
import io.qurani.android.model.CouponValidation
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.CouponDialog
import retrofit2.Call
import retrofit2.Response

class CouponPresenterImpl(private val dialog: CouponDialog) : Presenter.CouponPresenter {

    override fun validateCoupon(coupon: Coupon) {
        ApiService.apiClient!!.validateCoupon(coupon)
            .enqueue(object : CustomCallback<Data<CouponValidation>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        validateCoupon(coupon)
                    }
                }

                override fun onResponse(
                    call: Call<Data<CouponValidation>>,
                    response: Response<Data<CouponValidation>>
                ) {
                    if (response.body() != null) {
                        dialog.onCouponValidated(response.body()!!)
                    }
                }

            })
    }


}