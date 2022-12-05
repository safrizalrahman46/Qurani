package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Review
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.CourseReviewDialog
import retrofit2.Call
import retrofit2.Response

class CourseReviewPresenterImpl(private val dialog: CourseReviewDialog) :
    Presenter.CourseReviewPresenter {

    override fun addReview(review: Review) {
        ApiService.apiClient!!.addCourseReview(review)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        addReview(review)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onReviewSaved(response.body()!!, review)
                    }
                }

            })
    }

}