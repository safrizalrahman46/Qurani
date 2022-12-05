package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Assignment
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.MyAssignmentsFrag
import retrofit2.Call
import retrofit2.Response

class MyAssignmentsPresenterImpl(private val frag: MyAssignmentsFrag) :
    Presenter.MyAssignmentsPresenter {

    override fun getMyAssignments() {
        val myAssignments = ApiService.apiClient!!.getMyAssignments()
        frag.addNetworkRequest(myAssignments)
        myAssignments.enqueue(object : CustomCallback<Data<Data<List<Assignment>>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getMyAssignments()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Assignment>>>>,
                response: Response<Data<Data<List<Assignment>>>>
            ) {
                if (response.body() != null) {
                    frag.onAssignmentsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

}