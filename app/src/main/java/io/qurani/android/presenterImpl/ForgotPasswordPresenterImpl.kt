package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ForgetPassword
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.ForgetPasswordFrag
import retrofit2.Call
import retrofit2.Response

class ForgotPasswordPresenterImpl(private val frag: ForgetPasswordFrag): Presenter.ForgotPasswordPresenter {

    override fun sendChangePasswordLink(forgetPassword: ForgetPassword) {
        val sendChangePasswordLinkReq = ApiService.apiClient!!.sendChangePasswordLink(forgetPassword)
        frag.addNetworkRequest(sendChangePasswordLinkReq)
        sendChangePasswordLinkReq.enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse>{
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    sendChangePasswordLink(forgetPassword)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    frag.onPasswordChanged(response.body()!!)
                }
            }

        })
    }
}