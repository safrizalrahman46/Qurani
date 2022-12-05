package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.UserChangeSettings
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SettingsGeneralFrag
import retrofit2.Call
import retrofit2.Response

class SettingsGeneralPresenterImpl(private val frag: SettingsGeneralFrag) :
    Presenter.SettingsGeneralPresenter {

    override fun changeProfileSettings(changeSettings: UserChangeSettings) {
        ApiService.apiClient!!.changeProfileSettings(changeSettings)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        changeProfileSettings(changeSettings)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onSettingsChanged(response.body()!!, changeSettings)
                    }
                }

            })
    }
}