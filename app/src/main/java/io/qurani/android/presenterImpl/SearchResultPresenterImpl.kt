package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Data
import io.qurani.android.model.SearchObject
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.SearchResultFrag
import retrofit2.Call
import retrofit2.Response

class SearchResultPresenterImpl(private val searchFrag: SearchResultFrag) :
    Presenter.SearchResultPresenter {

    override fun search(s: String) {
        val searchCoursesAndUsers = ApiService.apiClient!!.searchCoursesAndUsers(s)
        searchFrag.addNetworkRequest(searchCoursesAndUsers)
        searchCoursesAndUsers.enqueue(object : CustomCallback<Data<SearchObject>> {
            override fun onResponse(
                call: Call<Data<SearchObject>>,
                response: Response<Data<SearchObject>>
            ) {
                if (response.body() != null) {
                    searchFrag.onSearchResultReceived(response.body()!!)
                }
            }

            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    search(s)
                }
            }

        })
    }
}