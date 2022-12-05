package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.model.Follow
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.PricingPlansDialog
import retrofit2.Call
import retrofit2.Response

class PricingPlansPresenterImpl(private val dialog: PricingPlansDialog) :
    Presenter.PricingPlansPresenter {

    override fun purchaseWithPoints(course: Course) {
        val retryListener =
            io.qurani.android.manager.net.RetryListener { purchaseWithPoints(course) }
        val callback = getCallback(retryListener)
        if (course.isBundle()) {
            ApiService.apiClient!!.bundlePurchaseWithPoints(course.id, Follow()).enqueue(callback)
        } else {
            ApiService.apiClient!!.purchaseWithPoints(course.id, Follow()).enqueue(callback)
        }
    }

    private fun getCallback(retryListener: io.qurani.android.manager.net.RetryListener): CustomCallback<io.qurani.android.model.BaseResponse> {
        return object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return retryListener
            }

            override fun onResponse(
                call: Call<io.qurani.android.model.BaseResponse>,
                res: Response<io.qurani.android.model.BaseResponse>
            ) {
                if (res.body() != null) {
                    dialog.onPurchase(res.body()!!)
                }
            }
        }
    }
}