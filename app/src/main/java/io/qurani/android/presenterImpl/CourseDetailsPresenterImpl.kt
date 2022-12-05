package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.AddToCart
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.CourseDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CourseDetailsPresenterImpl(private val frag: CourseDetailsFrag) :
    Presenter.CourseDetailsPresenter {

    override fun subscribe(addToCart: AddToCart) {
        ApiService.apiClient!!.subscribe(addToCart).enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    subscribe(addToCart)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    frag.onSubscribed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }

    override fun addCourseToUserCourse(courseId: Int) {
        val addFreeCourseReq = ApiService.apiClient!!.addFreeCourse(courseId)
        frag.addNetworkRequest(addFreeCourseReq)
        addFreeCourseReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    addCourseToUserCourse(courseId)
                }
            }

            override fun onResponse(
                call: Call<io.qurani.android.model.BaseResponse>,
                response: Response<io.qurani.android.model.BaseResponse>
            ) {
                if (response.body() != null) {
                    frag.onCourseAdded(response.body()!!)
                }
            }

            override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }
}