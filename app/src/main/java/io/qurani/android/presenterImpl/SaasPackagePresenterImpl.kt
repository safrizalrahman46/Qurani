package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Data
import io.qurani.android.model.SaasPackage
import io.qurani.android.model.SaasPackageItem
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SaasPackageFrag
import retrofit2.Call
import retrofit2.Response

class SaasPackagePresenterImpl(private val frag: SaasPackageFrag) : Presenter.SaasPackagePresenter {

    override fun getSaasPackages() {
        ApiService.apiClient!!.getSaasPackages()
            .enqueue(object : CustomCallback<Data<SaasPackage>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getSaasPackages()
                    }
                }

                override fun onResponse(
                    call: Call<Data<SaasPackage>>,
                    res: Response<Data<SaasPackage>>
                ) {
                    if (res.body() != null) {
                        frag.onSaasPackageReceived(res.body()!!.data!!)
                    }
                }

            })
    }

    override fun checkoutSubscription(saasPackageItem: SaasPackageItem) {
        ApiService.apiClient!!.checkoutSaasPackage(saasPackageItem)
            .enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        checkoutSubscription(saasPackageItem)
                    }
                }

                override fun onResponse(
                    call: Call<Data<io.qurani.android.model.Response>>,
                    res: Response<Data<io.qurani.android.model.Response>>
                ) {
                    if (res.body() != null) {
                        frag.onCheckout(res.body()!!)
                    }
                }

            })
    }
}