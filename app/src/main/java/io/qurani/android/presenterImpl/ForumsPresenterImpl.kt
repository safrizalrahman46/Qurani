package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.Forums
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.ForumsFrag
import retrofit2.Call
import retrofit2.Response

class ForumsPresenterImpl(private val frag: ForumsFrag) : Presenter.ForumsPresenter {

    override fun getForumQuestions(courseId: Int) {
        val courseForum = ApiService.apiClient!!.getCourseForum(courseId)
        frag.addNetworkRequest(courseForum)
        courseForum.enqueue(object : CustomCallback<Data<Forums>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getForumQuestions(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<Forums>>,
                response: Response<Data<Forums>>
            ) {
                if (response.body() != null) {
                    frag.onForumReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun searchInCourseForum(courseId: Int, s: String) {
        val searchInCourseForum = ApiService.apiClient!!.searchInCourseForum(courseId, s)
        frag.addNetworkRequest(searchInCourseForum)
        searchInCourseForum.enqueue(object : CustomCallback<Data<Forums>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getForumQuestions(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<Forums>>,
                response: Response<Data<Forums>>
            ) {
                if (response.body() != null) {
                    frag.onForumReceived(response.body()!!.data!!)
                }
            }
        })

    }
}