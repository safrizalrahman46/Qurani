package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.BundleCoursesFrag
import retrofit2.Call
import retrofit2.Response

class BundleCoursesPresenterImpl(private val frag: BundleCoursesFrag) :
    Presenter.BundleCoursesPresenter {

    override fun getBundleCourses(id: Int) {
        val coursesForBundle = ApiService.apiClient!!.getCoursesForBundle(id)
        frag.addNetworkRequest(coursesForBundle)
        coursesForBundle.enqueue(object : CustomCallback<Data<Data<List<Course>>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getBundleCourses(id)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Course>>>>,
                response: Response<Data<Data<List<Course>>>>
            ) {
                if (response.body() != null) {
                    frag.onCoursesReceived(response.body()!!.data!!.data!!)
                }
            }
        })
    }
}