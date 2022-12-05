package io.qurani.android.presenterImpl

import io.qurani.android.manager.App
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.AppConfig
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.SplashScreenActivity
import retrofit2.Call
import retrofit2.Response

class SplashScreenPresenterImpl(private val activity: SplashScreenActivity) :
    Presenter.SplashScreenPresenter {

    override fun getAppConfig() {
        val customerSettings = ApiService.apiClient?.getAppConfig()
        activity.addNetworkRequest(customerSettings)
        customerSettings?.enqueue(object : CustomCallback<AppConfig> {
            override fun onResponse(call: Call<AppConfig>, response: Response<AppConfig>) {
                if (response.body() != null) {
                    io.qurani.android.manager.App.appConfig = response.body()!!
                    activity.onAppConfigReceived()
                }
            }

            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getAppConfig()
                }
            }

            override fun onFailure(call: Call<AppConfig>, t: Throwable) {
                activity.showNoNetFrag()
            }

        })
    }
}