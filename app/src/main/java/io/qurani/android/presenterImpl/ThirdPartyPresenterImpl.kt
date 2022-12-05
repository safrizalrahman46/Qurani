package io.qurani.android.presenterImpl

import com.google.gson.Gson
import io.qurani.android.manager.App
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Data
import io.qurani.android.model.Response
import io.qurani.android.model.ThirdPartyLogin
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.abs.UserAuthFrag
import retrofit2.Call

open class ThirdPartyPresenterImpl(
    private val frag: UserAuthFrag
) : Presenter.ThirdPartyPresenter {

    override fun facebookSignInUp(thirdPartyLogin: ThirdPartyLogin) {
        val signUpRequest = ApiService.apiClient!!.registerWithFacebook(thirdPartyLogin)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    facebookSignInUp(thirdPartyLogin)
                }
            }

            override fun onResponse(
                call: Call<Data<io.qurani.android.model.Response>>,
                response: retrofit2.Response<Data<io.qurani.android.model.Response>>
            ) {
                onThridPartyRes(
                    response,
                    io.qurani.android.manager.App.Companion.RegistrationProvider.FACEBOOK.value(),
                    thirdPartyLogin
                )
            }

            override fun onFailure(call: Call<Data<io.qurani.android.model.Response>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }

    override fun googleSignInUp(thirdPartyLogin: ThirdPartyLogin) {
        val signUpRequest = ApiService.apiClient!!.registerWithGoogle(thirdPartyLogin)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    googleSignInUp(thirdPartyLogin)
                }
            }

            override fun onResponse(
                call: Call<Data<io.qurani.android.model.Response>>,
                response: retrofit2.Response<Data<io.qurani.android.model.Response>>
            ) {
                onThridPartyRes(
                    response,
                    io.qurani.android.manager.App.Companion.RegistrationProvider.GOOGLE.value(),
                    thirdPartyLogin
                )
            }

            override fun onFailure(call: Call<Data<io.qurani.android.model.Response>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }

    private fun onThridPartyRes(
        response: retrofit2.Response<Data<io.qurani.android.model.Response>>,
        provider: Int,
        thirdPartyLogin: ThirdPartyLogin
    ) {
        if (response.body() != null) {
            frag.onThirdPartyLogin(response.body()!!, provider, thirdPartyLogin)

        } else {
            val error = Gson().fromJson<io.qurani.android.model.BaseResponse>(
                response.errorBody()?.string(),
                io.qurani.android.model.BaseResponse::class.java
            )

            frag.onErrorOccured(error)
        }
    }
}