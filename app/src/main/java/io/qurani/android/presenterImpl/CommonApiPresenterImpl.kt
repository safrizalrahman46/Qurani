package io.qurani.android.presenterImpl

import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.lang.NumberFormatException

class CommonApiPresenterImpl private constructor() : Presenter.CommonApiPresenter {

    companion object {
        private lateinit var mPresenterImpl: CommonApiPresenterImpl

        fun getInstance(): CommonApiPresenterImpl {
            if (!this::mPresenterImpl.isInitialized) {
                mPresenterImpl = CommonApiPresenterImpl()
            }

            return mPresenterImpl
        }
    }

    override fun getQuickInfo(callback: ItemCallback<QuickInfo>) {
        ApiService.apiClient!!.getQuickInfo().enqueue(object : CustomCallback<QuickInfo> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getQuickInfo(callback)
                }
            }

            override fun onResponse(call: Call<QuickInfo>, response: Response<QuickInfo>) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!)
                }
            }

            override fun onFailure(call: Call<QuickInfo>, t: Throwable) {
                super.onFailure(call, t)
                callback.onFailed()
            }
        })
    }

    override fun addToCart(addToCart: AddToCart, callback: ItemCallback<io.qurani.android.model.BaseResponse>) {
        ApiService.apiClient!!.addToCart(addToCart).enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    addToCart(addToCart, callback)
                }
            }

            override fun onResponse(call: Call<io.qurani.android.model.BaseResponse>, response: Response<io.qurani.android.model.BaseResponse>) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!)
                }
            }

            override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                callback.onFailed()
            }
        })
    }

    override fun getUserInfo(userId: Int, callback: ItemCallback<UserProfile>) {
        ApiService.apiClient!!.getUserProfile(userId)
            .enqueue(object : CustomCallback<Data<Data<UserProfile>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getUserInfo(userId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<UserProfile>>>,
                    response: Response<Data<Data<UserProfile>>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!.data!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<UserProfile>>>, t: Throwable) {
                    super.onFailure(call, t)
                    callback.onFailed()
                }

            })
    }

    override fun getCourseDetails(courseId: Int, callback: ItemCallback<Course>) {
        ApiService.apiClient!!.getCourseDetails(courseId)
            .enqueue(object : CustomCallback<Data<Course>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        getCourseDetails(courseId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Course>>,
                    response: Response<Data<Course>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!)
                    }
                }

                override fun onFailure(call: Call<Data<Course>>, t: Throwable) {
                    super.onFailure(call, t)
                    callback.onFailed()
                }

            })
    }

    override fun getBundleDetails(bundleId: Int, callback: ItemCallback<Course>) {
        ApiService.apiClient!!.getBundleDetails(bundleId)
            .enqueue(object : CustomCallback<Data<Data<Course>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        getCourseDetails(bundleId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<Course>>>,
                    response: Response<Data<Data<Course>>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!.data!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<Course>>>, t: Throwable) {
                    super.onFailure(call, t)
                    callback.onFailed()
                }

            })
    }

    override fun getCourseContent(courseId: Int, callback: ItemCallback<List<Chapter>>) {
        ApiService.apiClient!!.getCourseContent(courseId)
            .enqueue(object : CustomCallback<Data<List<Chapter>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        getCourseContent(courseId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<Chapter>>>,
                    response: Response<Data<List<Chapter>>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!)
                    }
                }

            })
    }

    override fun getCourseCerts(courseId: Int, callback: ItemCallback<List<Quiz>>) {
        ApiService.apiClient!!.getCourseCertificates(courseId)
            .enqueue(object : CustomCallback<Data<List<Quiz>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                    return io.qurani.android.manager.net.RetryListener {
                        getCourseCerts(courseId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<Quiz>>>,
                    response: Response<Data<List<Quiz>>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!)
                    }
                }
            })
    }

    override fun getFileSize(url: String, sizeCallback: ItemCallback<Long>) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).head().build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                sizeCallback.onItem(0)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.body != null) {
                    try {
                        sizeCallback.onItem(response.headers["Content-Length"]!!.toLong())
                    } catch (ex: NullPointerException) {
                        sizeCallback.onItem(0)
                    } catch (ex: NumberFormatException) {
                        sizeCallback.onItem(0)
                    }
                } else {
                    sizeCallback.onItem(0)
                }
            }
        })
    }

}