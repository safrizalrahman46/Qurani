package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Follow
import io.qurani.android.presenter.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutPresenterImpl: Presenter.LogoutPresenter {
    override fun logout() {
        ApiService.apiClient!!.logout(Follow()).enqueue(object : Callback<io.qurani.android.model.BaseResponse> {
            override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {

            }
        })
    }
}