package io.qurani.android.presenterImpl

import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.AddToFav
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Course
import io.qurani.android.model.Follow
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ClassDetailsMoreDialog
import retrofit2.Call
import retrofit2.Response

class CourseDetailsMorePresenterImpl(private val dialog: ClassDetailsMoreDialog) :
    Presenter.CourseDetailsMorePresenter {

    override fun addToFavorite(addToFav: AddToFav) {
        ApiService.apiClient!!.addRemoveFromFavorite(addToFav)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        addToFavorite(addToFav)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onItemAddedToFavorites(response.body()!!)
                    }
                }

            })

    }

}