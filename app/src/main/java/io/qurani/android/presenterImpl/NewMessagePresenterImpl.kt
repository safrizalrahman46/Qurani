package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Message
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.NewMessageDialog
import retrofit2.Call
import retrofit2.Response

class NewMessagePresenterImpl(private val dialog: NewMessageDialog) :
    Presenter.NewMessagePresenter {


    override fun addMessage(userId: Int, message: Message) {
        ApiService.apiClient!!.addNewMessage(userId, message)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        addMessage(userId, message)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMessageAdded(response.body()!!)
                    }
                }

            })
    }
}