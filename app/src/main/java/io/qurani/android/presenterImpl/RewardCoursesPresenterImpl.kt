package io.qurani.android.presenterImpl

import io.qurani.android.manager.Utils.toInt
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.model.KeyValuePair
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.RewardCoursesFrag
import retrofit2.Call
import retrofit2.Response

class RewardCoursesPresenterImpl(private val frag: RewardCoursesFrag) :
    Presenter.RewardCoursesPresenter {

    override fun getRewardCourses(categories: List<KeyValuePair>?, options: List<KeyValuePair>?) {
        val filter = HashMap<String, String>()
        filter["reward"] = true.toInt().toString()

        if (!categories.isNullOrEmpty()){
            filter[categories[0].key] = categories[0].value
        }

        if (!options.isNullOrEmpty()) {
            for (option in options) {
                filter[option.key] = option.value
            }
        }

        ApiService.apiClient!!.getCourses(filter).enqueue(object : CustomCallback<Data<List<Course>>>{
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getRewardCourses(categories, options)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                res: Response<Data<List<Course>>>
            ) {
                if (res.body() != null) {
                    frag.onResultReceived(res.body()!!.data!!)
                }
            }

        })
    }
}