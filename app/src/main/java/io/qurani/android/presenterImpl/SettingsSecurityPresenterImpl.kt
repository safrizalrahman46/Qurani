package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ChangePassword
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SettingsSecurityFrag
import retrofit2.Call
import retrofit2.Response

class SettingsSecurityPresenterImpl(private val frag: SettingsSecurityFrag) : Presenter.SettingsSecurityPresenter {

    override fun changePassword(changePassword: ChangePassword) {
        ApiService.apiClient!!.changePassword(changePassword)
            .enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        changePassword(changePassword)
                    }
                }

                override fun onResponse(
                    call: Call<Data<io.qurani.android.model.Response>>,
                    response: Response<Data<io.qurani.android.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onPasswordChanges(response.body()!!)
                    }
                }

            })
    }

}