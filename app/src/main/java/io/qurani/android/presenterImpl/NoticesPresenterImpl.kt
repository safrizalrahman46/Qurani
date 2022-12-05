package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.Notice
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.NoticesFrag
import retrofit2.Call
import retrofit2.Response

class NoticesPresenterImpl(private val frag: NoticesFrag) : Presenter.NoticesPresenter {

    override fun getNotices(courseId: Int) {
        val notices = ApiService.apiClient!!.getNotices(courseId)
        frag.addNetworkRequest(notices)
        notices.enqueue(object : CustomCallback<Data<List<Notice>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getNotices(courseId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Notice>>>,
                response: Response<Data<List<Notice>>>
            ) {
                if (response.body() != null) {
                    frag.onNoticesReceived(response.body()!!.data!!)
                }
            }
        })
    }
}