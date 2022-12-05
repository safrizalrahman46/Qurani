package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.model.Quiz
import io.qurani.android.model.QuizResult
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.QuizzesFrag
import retrofit2.Call
import retrofit2.Response

class QuzziesPresenterImpl(private val frag: QuizzesFrag) : Presenter.QuzziesPresenter {

    override fun getMyResults() {
        val myQuizzesResultReq = ApiService.apiClient!!.getMyQuizzesResult()
        frag.addNetworkRequest(myQuizzesResultReq)
        myQuizzesResultReq
            .enqueue(object : CustomCallback<Data<Count<QuizResult>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getMyResults()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<QuizResult>>>,
                    response: Response<Data<Count<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onQuizzesResultRecevied(response.body()!!.data!!.items)
                    }
                }

            })
    }

    override fun getNotParticipated() {
        val notParticipatedQuizzesReq = ApiService.apiClient!!.getNotParticipatedQuizzes()
        frag.addNetworkRequest(notParticipatedQuizzesReq)
        notParticipatedQuizzesReq.enqueue(object : CustomCallback<Data<Count<Quiz>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getNotParticipated()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Quiz>>>,
                response: Response<Data<Count<Quiz>>>
            ) {
                if (response.body() != null) {
                    frag.onQuizListRecevied(response.body()!!.data!!.items)
                }
            }

        })
    }

    override fun getQuizList() {
        val quizzesListReq = ApiService.apiClient!!.getQuizzesList()
        frag.addNetworkRequest(quizzesListReq)
        quizzesListReq.enqueue(object : CustomCallback<Data<Count<Quiz>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getQuizList()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Quiz>>>,
                response: Response<Data<Count<Quiz>>>
            ) {
                if (response.body() != null) {
                    frag.onQuizListRecevied(response.body()!!.data!!.items)
                }
            }

        })
    }

    override fun getStudentResults() {
        val studentResultsReq = ApiService.apiClient!!.getStudentResults()
        frag.addNetworkRequest(studentResultsReq)
        studentResultsReq.enqueue(object : CustomCallback<Data<Count<QuizResult>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getStudentResults()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<QuizResult>>>,
                response: Response<Data<Count<QuizResult>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentResultRecevied(response.body()!!.data!!.items)
                }
            }
        })
    }
}