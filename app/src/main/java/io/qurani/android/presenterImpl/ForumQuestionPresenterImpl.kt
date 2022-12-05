package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ForumItem
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ForumQuestionDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class ForumQuestionPresenterImpl(val dialog: ForumQuestionDialog) :
    Presenter.ForumQuestionPresenter {

    fun getCallback(retryListener: io.qurani.android.manager.net.RetryListener): CustomCallback<io.qurani.android.model.BaseResponse> {
        val callback = object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return retryListener
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onResponse(response.body()!!)
                }
            }
        }
        return callback
    }

    override fun sendQuestion(courseId: Int, forumItem: ForumItem, file: File?) {
        var filePart: MultipartBody.Part? = null

        if (file != null) {
            val fileBody = file.asRequestBody()
            filePart = MultipartBody.Part.createFormData("attachment", file.name, fileBody)
        }

        ApiService.apiClient!!.postForumQuestion(
            courseId,
            MultipartBody.Part.createFormData("title", forumItem.title),
            MultipartBody.Part.createFormData("description", forumItem.description),
            filePart
        ).enqueue(getCallback { sendQuestion(courseId, forumItem, file) })
    }

    override fun editQuestion(forumItem: ForumItem, file: File?) {
        if (file == null) {
            ApiService.apiClient!!.editForumQuestion(
                forumItem.id,
                forumItem
            ).enqueue(getCallback { editQuestion(forumItem, file) })
        } else {
            val fileBody = file.asRequestBody()
            val filePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("attachment", file.name, fileBody)

            ApiService.apiClient!!.editForumQuestion(
                forumItem.id,
                MultipartBody.Part.createFormData("title", forumItem.title),
                MultipartBody.Part.createFormData("description", forumItem.description),
                filePart,
            ).enqueue(getCallback { editQuestion(forumItem, file) })
        }
    }
}