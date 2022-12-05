package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.StudentAssignments
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.StudentAssignmentsFrag
import retrofit2.Call
import retrofit2.Response

class StudentAssignmentsPresenterImpl(private val frag: StudentAssignmentsFrag) :
    Presenter.StudentAssignmentsPresenter {

    override fun getStudentAssignments() {
        val studentAssignments = ApiService.apiClient!!.getStudentAssignments()
        frag.addNetworkRequest(studentAssignments)
        studentAssignments.enqueue(object : CustomCallback<Data<StudentAssignments>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getStudentAssignments()
                }
            }

            override fun onResponse(
                call: Call<Data<StudentAssignments>>,
                response: Response<Data<StudentAssignments>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}