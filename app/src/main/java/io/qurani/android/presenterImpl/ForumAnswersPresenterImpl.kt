package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.ForumItemAnswer
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.ForumAnswersFrag
import retrofit2.Call
import retrofit2.Response

class ForumAnswersPresenterImpl(val frag: ForumAnswersFrag) : Presenter.ForumAnswersPresenter {

    override fun getForumQuestionAnswers(forumId: Int) {
        val forumQuestionAnswers = ApiService.apiClient!!.getForumQuestionAnswers(forumId)
        frag.addNetworkRequest(forumQuestionAnswers)
        forumQuestionAnswers.enqueue(object : CustomCallback<Data<Data<List<ForumItemAnswer>>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getForumQuestionAnswers(forumId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<ForumItemAnswer>>>>,
                response: Response<Data<Data<List<ForumItemAnswer>>>>
            ) {
                if (response.body() != null) {
                    frag.onAnswersReceived(response.body()!!.data!!.data!!)
                }
            }
        })
    }
}