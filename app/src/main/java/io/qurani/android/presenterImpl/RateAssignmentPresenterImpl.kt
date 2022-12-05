package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Grade
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.RateAssignmentDialog
import retrofit2.Call
import retrofit2.Response

class RateAssignmentPresenterImpl(private val dialog: RateAssignmentDialog) :
    Presenter.RateAssignmentPresenter {

    override fun rateAssignment(assignmentId: Int, grade: Grade) {
        ApiService.apiClient!!.rateAssignment(assignmentId, grade)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        rateAssignment(assignmentId, grade)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onResponse(response.body()!!)
                    } else {
                        dialog.onRequestFailed()
                    }
                }

                override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onRequestFailed()
                }
            })
    }
}