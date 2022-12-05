package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Assignment
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.AssignmentOverviewFrag
import retrofit2.Call
import retrofit2.Response

class AssignmentOverviewPresenterImpl(val frag: AssignmentOverviewFrag) :
    Presenter.AssignmentOverviewPresenter {

    override fun getAssignmentStudents(assignmentId: Int) {
        val assignmentStudents = ApiService.apiClient!!.getAssignmentStudents(assignmentId)
        frag.addNetworkRequest(assignmentStudents)
        assignmentStudents.enqueue(object : CustomCallback<Data<List<Assignment>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getAssignmentStudents(assignmentId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Assignment>>>,
                response: Response<Data<List<Assignment>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getAssignment(assignmentId: Int) {
        val assignment = ApiService.apiClient!!.getAssignment(assignmentId)
        frag.addNetworkRequest(assignment)
        assignment.enqueue(object : CustomCallback<Data<Assignment>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getAssignmentStudents(assignmentId)
                }
            }

            override fun onResponse(
                call: Call<Data<Assignment>>,
                response: Response<Data<Assignment>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentReceived(response.body()!!.data!!)
                }
            }
        })
    }

}