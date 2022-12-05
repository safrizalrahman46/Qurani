package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Category
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.CategoriesFrag
import retrofit2.Call
import retrofit2.Response

open class CategoriesPresenterImpl(private val frag: CategoriesFrag) : Presenter.CategoriesPresenter {

    override fun getTrendingCategories() {
        val trendingCategories = ApiService.apiClient!!.getTrendingCategories()
        frag.addNetworkRequest(trendingCategories)
        trendingCategories
            .enqueue(object : CustomCallback<Data<Count<Category>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getTrendingCategories()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<Category>>>,
                    response: Response<Data<Count<Category>>>
                ) {
                    if (response.body() != null)
                        frag.onTrendingCategoriesRecevied(response.body()!!)
                }

            })
    }

    override fun getCategories() {
        val categories = ApiService.apiClient!!.getCategories()
        frag.addNetworkRequest(categories)
        categories.enqueue(object : CustomCallback<Data<Count<Category>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getCategories()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Category>>>,
                response: Response<Data<Count<Category>>>
            ) {
                if (response.body() != null)
                    frag.onCategoriesRecevied(response.body()!!)
            }

        })
    }

}