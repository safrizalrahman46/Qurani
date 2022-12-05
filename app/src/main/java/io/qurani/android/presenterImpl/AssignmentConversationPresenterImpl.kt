package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Assignment
import io.qurani.android.model.Conversation
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.AssignmentConversationFrag
import retrofit2.Call
import retrofit2.Response

class AssignmentConversationPresenterImpl(private val frag: AssignmentConversationFrag) :
    Presenter.AssignmentConversationPresenter {

    override fun getConversations(assignment: Assignment, studentId: Int?) {
        val assignmentConversations =
            ApiService.apiClient!!.getAssignmentConversations(assignment.id, studentId)
        frag.addNetworkRequest(assignmentConversations)
        assignmentConversations.enqueue(object : CustomCallback<Data<List<Conversation>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getConversations(assignment, studentId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Conversation>>>,
                response: Response<Data<List<Conversation>>>
            ) {
                if (response.body() != null) {
                    frag.onConversationsReceived(response.body()!!.data!!)
                }
            }
        })
    }

}