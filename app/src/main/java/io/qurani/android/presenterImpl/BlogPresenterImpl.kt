package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Blog
import io.qurani.android.model.Category
import io.qurani.android.model.Count
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.BlogsFrag
import retrofit2.Call
import retrofit2.Response

class BlogPresenterImpl(private val frag: BlogsFrag) : Presenter.BlogPresenter {

    override fun getBlogs() {
        val blogs = ApiService.apiClient!!.getBlogs()
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getBlogs()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getBlogs(catId: Int) {
        val blogs = ApiService.apiClient!!.getBlogs(catId)
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    getBlogs(catId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }
}