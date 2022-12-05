package io.qurani.android.presenterImpl

import com.google.gson.Gson
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Data
import io.qurani.android.model.User
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.UserRegistrationDialog
import retrofit2.Call
import retrofit2.Response

class UserRegistrationPresenterImpl(private val dialog: UserRegistrationDialog) :
    Presenter.UserRegistrationPresenter {

    override fun register(user: User) {
        val registerUserRequest = ApiService.apiClient!!.registerUser(user)
        dialog.addNetworkRequest(registerUserRequest)
        registerUserRequest.enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {

            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    register(user)
                }
            }

            override fun onResponse(call: Call<Data<io.qurani.android.model.Response>>,
                                    response: Response<Data<io.qurani.android.model.Response>>) {
                if (response.body() != null) {
                    dialog.onRegistrationSaved(response.body()!!, user)

                } else {
                    val error = Gson().fromJson<io.qurani.android.model.BaseResponse>(
                        response.errorBody()?.string(),
                        io.qurani.android.model.BaseResponse::class.java
                    )

                    dialog.onErrorOccured(error)
                }
            }
        })
    }
}