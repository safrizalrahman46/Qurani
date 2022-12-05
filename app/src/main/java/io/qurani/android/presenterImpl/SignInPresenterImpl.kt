package io.qurani.android.presenterImpl

import com.google.gson.Gson
import io.qurani.android.manager.net.ApiService
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Login
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SignInFrag
import retrofit2.Call
import retrofit2.Response
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data

class SignInPresenterImpl(private val frag: SignInFrag) : ThirdPartyPresenterImpl(frag),
    Presenter.SignInPresenter {

    override fun login(login: Login) {
        val loginRequest = ApiService.apiClient!!.login(login)
        frag.addNetworkRequest(loginRequest)
        loginRequest.enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {

            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    login(login)
                }
            }

            override fun onResponse(
                call: Call<Data<io.qurani.android.model.Response>>,
                response: Response<Data<io.qurani.android.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onSuccessfulLogin(response.body()!!)
                } else {
                    val error = Gson().fromJson<io.qurani.android.model.BaseResponse>(
                        response.errorBody()?.string(),
                        io.qurani.android.model.BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }

            override fun onFailure(
                call: Call<Data<io.qurani.android.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }
}