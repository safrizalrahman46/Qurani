package io.qurani.android.presenterImpl

import android.os.Looper
import android.util.Log
import io.qurani.android.manager.App
import io.qurani.android.manager.BuildVars
import io.qurani.android.manager.Utils
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.QuizResult
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.CertificateDetailsFrag
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class CertificateDetailsPresenterImpl(private val frag: CertificateDetailsFrag) :
    Presenter.CertificateDetailsPresenter {

    override fun getStudents() {
        val certificateStudentsReq = ApiService.apiClient!!.getCertificateStudents()
        frag.addNetworkRequest(certificateStudentsReq)
        certificateStudentsReq.enqueue(object : CustomCallback<Data<List<QuizResult>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getStudents()
                }
            }

            override fun onResponse(
                call: Call<Data<List<QuizResult>>>,
                response: Response<Data<List<QuizResult>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}