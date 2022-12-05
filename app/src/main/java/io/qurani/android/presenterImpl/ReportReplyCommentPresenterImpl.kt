package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Comment
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.CommentDialog
import retrofit2.Call
import retrofit2.Response

class ReportReplyCommentPresenterImpl(private val dialog: CommentDialog) :

    Presenter.ReportReplyCommentPresenter {
    override fun comment(comment: Comment) {
        val commentReq = ApiService.apiClient!!.comment(comment)
        dialog.addNetworkRequest(commentReq)
        commentReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    comment(comment)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!, comment)
                }
            }
        })
    }

    override fun reply(comment: Comment) {
        val replyReq = ApiService.apiClient!!.reply(comment.id, comment)
        dialog.addNetworkRequest(replyReq)
        replyReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    reply(comment)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!, comment)
                }
            }
        })
    }

    override fun reportComment(comment: Comment) {
        val reportCommentReq = ApiService.apiClient!!.reportComment(comment.id, comment)
        dialog.addNetworkRequest(reportCommentReq)
        reportCommentReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    reportComment(comment)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!, comment)
                }
            }
        })
    }

    override fun editComment(comment: Comment) {
        val editCommentReq = ApiService.apiClient!!.editComment(comment.id, comment)
        dialog.addNetworkRequest(editCommentReq)
        editCommentReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    editComment(comment)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!, comment)
                }
            }
        })
    }

    override fun reportCourse(comment: Comment) {
        val reportCourseReq = ApiService.apiClient!!.reportCourse(comment.id, comment)
        dialog.addNetworkRequest(reportCourseReq)
        reportCourseReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    reportCourse(comment)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    dialog.onRsp(response.body()!!, comment)
                }
            }
        })
    }

    override fun getReasons() {
        ApiService.apiClient!!.getReportReasons()
            .enqueue(object : CustomCallback<Data<Map<String, String>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getReasons()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Map<String, String>>>,
                    response: Response<Data<Map<String, String>>>
                ) {
                    if (response.body() != null) {
                        dialog.onReasonsReceived(response.body()!!.data!!.values)
                    }
                }
            })
    }
}