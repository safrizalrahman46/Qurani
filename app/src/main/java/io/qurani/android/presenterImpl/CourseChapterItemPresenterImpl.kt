package io.qurani.android.presenterImpl

import io.qurani.android.manager.App
import io.qurani.android.manager.Utils
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.OnDownloadProgressListener
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.frag.BaseCourseChapterItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class CourseChapterItemPresenterImpl(private val frag: BaseCourseChapterItem) :
    Presenter.CourseChapterItemPresenter {

    companion object {
        val downloadingRequests: HashMap<Int, Call<*>> = HashMap()
    }

    override fun getSessionItemDetails(url: String, callback: ItemCallback<ChapterSessionItem>) {
        val sessionChapterItemDetails = ApiService.apiClient!!.getSessionChapterItemDetails(url)
        frag.addNetworkRequest(sessionChapterItemDetails)
        sessionChapterItemDetails.enqueue(object : CustomCallback<Data<ChapterSessionItem>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getSessionItemDetails(url, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<ChapterSessionItem>>,
                response: Response<Data<ChapterSessionItem>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getFileItemDetails(url: String, callback: ItemCallback<ChapterFileItem>) {
        val fileChapterItemDetails = ApiService.apiClient!!.getFileChapterItemDetails(url)
        frag.addNetworkRequest(fileChapterItemDetails)
        fileChapterItemDetails.enqueue(object : CustomCallback<Data<ChapterFileItem>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getFileItemDetails(url, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<ChapterFileItem>>,
                response: Response<Data<ChapterFileItem>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }

//    override fun getTextLessonItemDetails(url: String, callback: ItemCallback<ChapterTextItem>) {
//        ApiService.apiClient!!.getTextLessonChapterItemDetails(url)
//            .enqueue(object : CustomCallback<Data<ChapterTextItem>> {
//                override fun onStateChanged(): RetryListener {
//                    return RetryListener {
//                        getTextLessonItemDetails(url, callback)
//                    }
//                }
//
//                override fun onResponse(
//                    call: Call<Data<ChapterTextItem>>,
//                    response: Response<Data<ChapterTextItem>>
//                ) {
//                    if (response.body() != null) {
//                        callback.onItem(response.body()!!.data!!)
//                    }
//                }
//            })
//    }

    override fun changeItemStatus(chapterItemMark: ChapterItemMark) {
        ApiService.apiClient!!.changeLessonItemStatus(chapterItemMark.courseId, chapterItemMark)
            .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        changeItemStatus(chapterItemMark)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onItemStatusChanged(response.body()!!, chapterItemMark)
                    }
                }
            })
    }

    override fun downloadFile(
        fileItem: ChapterFileItem,
        progressListener: io.qurani.android.manager.net.OnDownloadProgressListener
    ) {
        val fileId = fileItem.id

        val baseUrlAndHost = Utils.getBaseUrlAndHostFromUrl(fileItem.file) ?: return

        val downloadRequest =
            ApiService.getDownloadApiClient(baseUrlAndHost[0], progressListener, fileId)
                .download(baseUrlAndHost[1])

        downloadingRequests[fileId] = downloadRequest

        downloadRequest.enqueue(object : CustomCallback<ResponseBody> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                return io.qurani.android.manager.net.RetryListener {
                    downloadFile(fileItem, progressListener)
                }
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.body() != null) {
                    frag.context?.let {

                        Thread {
                            val byteStream = response.body()!!.byteStream()
                            val filePath = Utils.saveFile(
                                frag.requireContext(),
                                io.qurani.android.manager.App.Companion.Directory.DOWNLOADS.value(),
                                "${fileId}.${fileItem.fileType}",
                                byteStream
                            )

                            if (filePath != null) {
                                Utils.copyFileToDownloads(
                                    frag.requireContext(),
                                    File(filePath),
                                    "${fileItem.title}.${fileItem.fileType}"
                                )
                            }

                        }.start()
                    }
                }
            }
        })
    }

    override fun cancelDownload(fileId: Int) {
        if (downloadingRequests.containsKey(fileId)) {
            downloadingRequests[fileId]!!.cancel()
            downloadingRequests.remove(fileId)
        }
    }

    override fun getTextLessons(textLessonId: Int, callback: ItemCallback<List<ChapterTextItem>>) {
        val textLessons = ApiService.apiClient!!.getTextLessons(textLessonId)
        frag.addNetworkRequest(textLessons)
        textLessons.enqueue(object :
            CustomCallback<Data<List<ChapterTextItem>>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getTextLessons(textLessonId, callback)
                }
            }

            override fun onResponse(
                call: Call<Data<List<ChapterTextItem>>>,
                response: Response<Data<List<ChapterTextItem>>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }
}