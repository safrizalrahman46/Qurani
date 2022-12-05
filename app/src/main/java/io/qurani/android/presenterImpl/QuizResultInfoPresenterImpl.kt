package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.QuizResult
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.QuizResultInfoFrag
import retrofit2.Call
import retrofit2.Response

class QuizResultInfoPresenterImpl(private val frag: QuizResultInfoFrag) :
    Presenter.QuizResultInfoPresenter {

    override fun startQuiz(id: Int) {
        val startQuizReq = ApiService.apiClient!!.startQuiz(id)
        frag.addNetworkRequest(startQuizReq)
        startQuizReq.enqueue(object : CustomCallback<Data<QuizResult>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    startQuiz(id)
                }
            }

            override fun onResponse(
                call: Call<Data<QuizResult>>,
                response: Response<Data<QuizResult>>
            ) {
                if (response.body() != null && response.body()!!.isSuccessful) {
                    frag.onQuizStartBegin(response.body()!!)
                } else {
                    frag.cannotStartQuiz(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Data<QuizResult>>, t: Throwable) {
                super.onFailure(call, t)
                frag.cannotStartQuiz(null)
            }

        })
    }

    override fun getQuizResult(quizId: Int) {
        val quizResultReq = ApiService.apiClient!!.getQuizResult(quizId)
        frag.addNetworkRequest(quizResultReq)
        quizResultReq.enqueue(object : CustomCallback<Data<QuizResult>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getQuizResult(quizId)
                }
            }

            override fun onResponse(
                call: Call<Data<QuizResult>>,
                response: Response<Data<QuizResult>>
            ) {
                if (response.body() != null) {
                    frag.initStudentResult(response.body()!!.data!!)
                }
            }

            override fun onFailure(call: Call<Data<QuizResult>>, t: Throwable) {
                super.onFailure(call, t)
                frag.cannotStartQuiz(null)
            }

        })

    }
}