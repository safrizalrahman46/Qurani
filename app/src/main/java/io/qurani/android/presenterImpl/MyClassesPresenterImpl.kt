package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.model.MyClasses
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.MyClassesFrag
import io.qurani.android.ui.frag.MyClassesTabFrag
import retrofit2.Call
import retrofit2.Response

class MyClassesPresenterImpl(private val frag: MyClassesFrag) : Presenter.MyClassesPresenter {

    override fun getMyClasses() {
        val myClassesPageDataReq = ApiService.apiClient!!.getMyClassesPageData()
        frag.addNetworkRequest(myClassesPageDataReq)
        myClassesPageDataReq.enqueue(object : CustomCallback<MyClasses> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getMyClasses()
                }
            }

            override fun onResponse(call: Call<MyClasses>, response: Response<MyClasses>) {
                if (response.body() != null) {
                    frag.onMyClassesReceived(response.body()!!)
                }
            }

        })
    }

    override fun getPurchased() {
        val myPurchasesReq = ApiService.apiClient!!.getMyPurchases()
        frag.addNetworkRequest(myPurchasesReq)
        myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getPurchased()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Course>>>,
                response: Response<Data<Count<Course>>>
            ) {

                if (response.body() != null) {
                    frag.onPurchasedReceived(response.body()!!.data!!.items, true)
                }
            }

        })
    }

    override fun getOrganizations() {
        val myPurchasesReq = ApiService.apiClient!!.getCoursesOfOrganizations()
        frag.addNetworkRequest(myPurchasesReq)
        myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getPurchased()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Course>>>,
                response: Response<Data<Count<Course>>>
            ) {

                if (response.body() != null) {
                    frag.onPurchasedReceived(response.body()!!.data!!.items, false)
                }
            }

        })
    }

}