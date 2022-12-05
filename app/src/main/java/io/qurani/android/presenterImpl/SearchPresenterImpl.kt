package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.HomeFrag
import io.qurani.android.ui.frag.SearchFrag
import retrofit2.Call
import retrofit2.Response

class SearchPresenterImpl(private val frag: SearchFrag) : Presenter.SearchPresenter {

    override fun getBestRatedCourses() {
        val map = HashMap<String, String>()
        map["offset"] = "0"
        map["limit"] = "3"
        map["sort"] = "best_rates"

        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getBestRatedCourses()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onBestRatedCoursesRecevied(response.body()!!)
                }
            }
        })
    }
}