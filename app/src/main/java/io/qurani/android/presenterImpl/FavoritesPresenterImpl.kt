package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.FavoritesFrag
import retrofit2.Call
import retrofit2.Response

class FavoritesPresenterImpl(private val frag: FavoritesFrag) : Presenter.FavoritesPresenter {

    override fun getFavorites() {
        val favoritesReq = ApiService.apiClient!!.getFavorites()
        frag.addNetworkRequest(favoritesReq)
        favoritesReq.enqueue(object : CustomCallback<Data<Count<Favorite>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        getFavorites()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<Favorite>>>,
                    response: Response<Data<Count<Favorite>>>
                ) {
                    if (response.body() != null) {
                        frag.onFavoritesReceived(response.body()!!.data!!.items)
                    }
                }

            })
    }

    override fun removeFromFavorite(addToFav: AddToFav, adapterPosition: Int) {
        ApiService.apiClient!!.addRemoveFromFavorite(addToFav)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        removeFromFavorite(addToFav, adapterPosition)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onItemRemoved(response.body()!!, adapterPosition)
                    }
                }

            })
    }
}