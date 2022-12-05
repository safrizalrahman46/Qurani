package io.qurani.android.presenterImpl

import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Comments
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.CommentsFrag
import retrofit2.Call
import retrofit2.Response

class CommentsPresenterImpl() : Presenter.CommentsPresenter {

    override fun getComments(callback: ItemCallback<Comments>) {
        ApiService.apiClient!!.getComments().enqueue(object : CustomCallback<Data<Comments>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getComments(callback)
                }
            }

            override fun onResponse(
                call: Call<Data<Comments>>,
                response: Response<Data<Comments>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }
}