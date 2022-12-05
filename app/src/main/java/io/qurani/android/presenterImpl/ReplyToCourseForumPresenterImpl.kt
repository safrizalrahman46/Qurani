package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ForumItem
import io.qurani.android.model.Reply
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ForumReplyDialog
import retrofit2.Call
import retrofit2.Response

class ReplyToCourseForumPresenterImpl(val dialog: ForumReplyDialog) :
    Presenter.ReplyToCourseForumPresenter {

    override fun reply(forum: ForumItem, reply: Reply) {
        if (forum.isAnswer()) {
            editReply(forum.id, reply)
        } else {
            replyToQuestion(forum.id, reply)
        }
    }

    private fun editReply(id: Int, reply: Reply) {
        ApiService.apiClient!!.editReplyToForumQuestion(id, reply)
            .enqueue(getCallback { editReply(id, reply) })
    }

    private fun replyToQuestion(id: Int, reply: Reply) {
        ApiService.apiClient!!.replyToForumQuestion(id, reply)
            .enqueue(getCallback { replyToQuestion(id, reply) })
    }

    fun getCallback(retryListener: io.qurani.android.manager.net.RetryListener): CustomCallback<io.qurani.android.model.BaseResponse> {
        return object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return retryListener
            }

            override fun onResponse(
                call: Call<io.qurani.android.model.BaseResponse>,
                response: Response<io.qurani.android.model.BaseResponse>
            ) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!)
                }
            }
        }
    }

}