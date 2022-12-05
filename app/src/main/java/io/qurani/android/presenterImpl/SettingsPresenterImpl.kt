package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.ProfilePhoto
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SettingsFrag
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SettingsPresenterImpl(private val frag: SettingsFrag) :
    Presenter.SettingsPresenter {

    override fun uploadPhoto(path: String) {
        val file = File(path)
        val fileBody = file.asRequestBody()
        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("profile_image", file.name, fileBody)
        ApiService.apiClient!!.changeProfileImage(filePart)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        uploadPhoto(path)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onProfileSaved(response.body()!!)
                    }
                }

            })
    }
}