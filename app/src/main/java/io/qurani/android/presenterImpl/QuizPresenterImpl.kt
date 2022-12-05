package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.QuizAnswer
import io.qurani.android.model.QuizResult
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.QuizFrag
import retrofit2.Call
import retrofit2.Response

class QuizPresenterImpl(private val frag: QuizFrag) : Presenter.QuizPresenter {

    override fun storeResult(quizId: Int, answer: QuizAnswer) {
        ApiService.apiClient!!.storeQuizResult(quizId, answer)
            .enqueue(object : CustomCallback<Data<Data<QuizResult>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        storeResult(quizId, answer)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<QuizResult>>>,
                    response: Response<Data<Data<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onQuizResultSaved(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<QuizResult>>>, t: Throwable) {
                    super.onFailure(call, t)
                    frag.onRequestFailed()
                }
            })
    }
}