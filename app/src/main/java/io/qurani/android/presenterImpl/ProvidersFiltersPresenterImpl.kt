package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Category
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ProvidersFiltersDialog
import retrofit2.Call
import retrofit2.Response

class ProvidersFiltersPresenterImpl(private val dialog: ProvidersFiltersDialog) :
    Presenter.ProvidersFiltersPresenter {

    override fun getCategories() {
        val categories = ApiService.apiClient!!.getCategories()
        dialog.addNetworkRequest(categories)
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
                    dialog.onCategoriesRecevied(response.body()!!)
            }

        })
    }
}