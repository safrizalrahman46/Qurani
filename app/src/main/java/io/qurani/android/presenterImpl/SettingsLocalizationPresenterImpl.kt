package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Data
import io.qurani.android.model.Region
import io.qurani.android.model.UserChangeLocalization
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SettingsLocalizationFrag
import retrofit2.Call
import retrofit2.Response

class SettingsLocalizationPresenterImpl(private val frag: SettingsLocalizationFrag) :
    Presenter.SettingsLocalizationPresenter {

    interface RegionCallback {
        fun onRegionsReceived(regions: List<Region>)
    }

    override fun getTimeZones() {
        val timeZonesReq = ApiService.apiClient!!.getTimeZones()
        frag.addNetworkRequest(timeZonesReq)
        timeZonesReq.enqueue(object : CustomCallback<Data<List<String>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getTimeZones()
                }
            }

            override fun onResponse(
                call: Call<Data<List<String>>>,
                res: Response<Data<List<String>>>
            ) {
                if (res.body() != null) {
                    frag.onTimeZonesReceived(res.body()!!.data!!)
                }
            }

        })
    }

    override fun getCountries() {
        val countriesReq = ApiService.apiClient!!.getCountries()
        frag.addNetworkRequest(countriesReq)
        getRegion(countriesReq, object : RegionCallback {
            override fun onRegionsReceived(regions: List<Region>) {
                frag.onCountriesReceived(regions)
            }
        })
    }

    override fun getProvinces(countryId: Int) {
        val provincesReq = ApiService.apiClient!!.getProvinces(countryId)
        frag.addNetworkRequest(provincesReq)
        getRegion(provincesReq, object : RegionCallback {
            override fun onRegionsReceived(regions: List<Region>) {
                frag.onProvincesReceived(regions)
            }
        })
    }

    override fun getCities(provinceId: Int) {
        val citiesReq = ApiService.apiClient!!.getCities(provinceId)
        frag.addNetworkRequest(citiesReq)
        getRegion(citiesReq, object : RegionCallback {
            override fun onRegionsReceived(regions: List<Region>) {
                frag.onCitiesReceived(regions)
            }
        })
    }

    override fun getDistricts(cityId: Int) {
        val districtsReq = ApiService.apiClient!!.getDistricts(cityId)
        frag.addNetworkRequest(districtsReq)
        getRegion(districtsReq, object : RegionCallback {
            override fun onRegionsReceived(regions: List<Region>) {
                frag.onDistrictsReceived(regions)
            }
        })
    }

    override fun changeProfileSettings(changeSettings: UserChangeLocalization) {
        ApiService.apiClient!!.changeProfileSettings(changeSettings)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        changeProfileSettings(changeSettings)
                    }
                }

                override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, res: Response<io.qurani.android.model.BaseResponse>) {
                    if (res.body() != null) {
                        frag.onSettingsChanged(res.body()!!, changeSettings)
                    }
                }
            })
    }


    fun getRegion(
        regionReq: Call<Data<List<Region>>>,
        callback: RegionCallback
    ) {
        regionReq.enqueue(object : CustomCallback<Data<List<Region>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getRegion(regionReq, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Region>>>,
                res: Response<Data<List<Region>>>
            ) {
                if (res.body() != null) {
                    callback.onRegionsReceived(res.body()!!.data!!)
                }
            }

        })
    }
}