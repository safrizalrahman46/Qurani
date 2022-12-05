package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Conversation
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.AssignmentConversationDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class AssignmentNewConversationPresenterImpl(private val dialog: AssignmentConversationDialog) :
    Presenter.AssignmentNewConversationPresenter {

    override fun saveConversation(assignmentId: Int, conversation: Conversation, file: File?) {
        val fileBody = file?.asRequestBody()
        val filePart = fileBody?.let {
            MultipartBody.Part.createFormData("file_path", file.name,
                it
            )
        }

        var studentPart: MultipartBody.Part? = null
        if (conversation.studentId > 0) {
            studentPart = MultipartBody.Part.createFormData(
                "student_id",
                conversation.studentId.toString()
            )
        }

        ApiService.apiClient!!.saveAssignmentConversation(
            assignmentId,
            MultipartBody.Part.createFormData("file_title", conversation.fileTitle),
            MultipartBody.Part.createFormData("message", conversation.message),
            filePart,
            studentPart
        ).enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    saveConversation(assignmentId, conversation, file)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onResponse(response.body()!!)
                }
            }
        })
    }
}