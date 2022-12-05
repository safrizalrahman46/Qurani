package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.model.FinancialSettings
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SettingsFinancialFrag
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SettingsFinancialPresenterImpl(private val frag: SettingsFinancialFrag) :
    Presenter.SettingsFinancialPresenter {

    override fun uploadFinancialSettings(financialSettings: FinancialSettings) {
        ApiService.apiClient!!.changeProfileSettings(financialSettings)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        uploadFinancialSettings(financialSettings)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onSettingsSaved(response.body()!!, financialSettings)
                    }
                }

            })
    }

    override fun getAccountTypes() {
        ApiService.apiClient!!.getAccountTypes().enqueue(object :
            CustomCallback<Data<Count<String>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getAccountTypes()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<String>>>,
                response: Response<Data<Count<String>>>
            ) {
                if (response.body() != null) {
                    frag.onAccountTypesReceived(response.body()!!.data!!)
                }
            }

        })
    }

    override fun uploadFinancialSettingsFiles(identityFile: File, certFile: File) {
        val identityFileBody = identityFile.asRequestBody()
        val identityFilePart =
            MultipartBody.Part.createFormData("identity_scan", identityFile.name, identityFileBody)

        val certFileBody = certFile.asRequestBody()
        val certFilePart =
            MultipartBody.Part.createFormData("certificate", certFile.name, certFileBody)

        ApiService.apiClient!!.uploadFinanicalSettings(identityFilePart, certFilePart)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        uploadFinancialSettingsFiles(identityFile, certFile)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onFilesSaved(response.body()!!)
                    }
                }

            })
    }
}