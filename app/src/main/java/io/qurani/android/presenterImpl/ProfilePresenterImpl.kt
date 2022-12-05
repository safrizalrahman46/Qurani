package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.UserProfile
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.ProfileFrag
import retrofit2.Call
import retrofit2.Response

class ProfilePresenterImpl(private val frag: ProfileFrag) : Presenter.ProfilePresenter {

    override fun getUserProfile(userId: Int) {
        val userProfileReq = ApiService.apiClient!!.getUserProfile(userId)
        frag.addNetworkRequest(userProfileReq)
        userProfileReq.enqueue(object : CustomCallback<Data<Data<UserProfile>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getUserProfile(userId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<UserProfile>>>,
                response: Response<Data<Data<UserProfile>>>
            ) {
                if (response.body() != null) {
                    frag.onUserProfileReceived(response.body()!!.data!!)
                }
            }

        })
    }
}