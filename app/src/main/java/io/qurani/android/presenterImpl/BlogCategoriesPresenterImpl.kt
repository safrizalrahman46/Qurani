package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Category
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.BlogCategoriesDialog
import retrofit2.Call
import retrofit2.Response

class BlogCategoriesPresenterImpl(private val dialog: BlogCategoriesDialog) : Presenter.BlogCategoriesPresenter {
    override fun getBlogCategories() {
        ApiService.apiClient!!.getBlogCategories()
            .enqueue(object : CustomCallback<Data<List<Category>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getBlogCategories()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<Category>>>,
                    response: Response<Data<List<Category>>>
                ) {
                    if (response.body() != null) {
                        dialog.onBlogCatsReceived(response.body()!!.data!!)
                    }
                }

            })
    }
}