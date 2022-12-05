package io.qurani.android.presenterImpl

import io.qurani.android.manager.MimeTypes
import io.qurani.android.manager.Utils
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.OnDownloadProgressListener
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.ProgressiveLoadingDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProgressiveLoadingDialogPresenterImpl(private val dialog: ProgressiveLoadingDialog) :
    Presenter.ProgressiveLoadingPresenter {

    companion object {
        private const val TAG = "ProgressiveLoadingDialo"
    }

    override fun downloadFile(
        fileDir: String?,
        fileUrl: String,
        downloadListener: io.qurani.android.manager.net.OnDownloadProgressListener,
        toDownloads: Boolean,
        fileNameFromHeader: Boolean,
        defaultFileName: String
    ) {
        val baseUrlAndHost = Utils.getBaseUrlAndHostFromUrl(fileUrl) ?: return

        val download = ApiService.getDownloadApiClient(baseUrlAndHost[0], downloadListener, null)
            .download(baseUrlAndHost[1])
        dialog.addNetworkRequest(download)
        download.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.body() != null) {
                    dialog.context?.let {
                        Thread {
                            try {
                                val filename: String = if (fileNameFromHeader) {
                                    val disposition = response.headers()["Content-Disposition"]
                                    val contentType = response.headers()["Content-Type"]
                                    if (disposition != null) {
                                        val contentSplit = disposition.split("filename=")
                                        contentSplit[1].replace("filename=", "")
                                            .replace("\"", "").trim();
                                    } else {
                                        var fileExtension =
                                            contentType?.let { it ->
                                                MimeTypes.getFileExtensionFromMimeType(
                                                    it
                                                )
                                            }
                                        if (fileExtension == null) {
                                            fileExtension = ""
                                        }

                                        "$defaultFileName$fileExtension"
                                    }
                                } else {
                                    Utils.extractFileNameFromUrl(fileUrl)
                                }

                                val byteStream = response.body()!!.byteStream()
                                val filePath = Utils.saveFile(
                                    dialog.requireContext(),
                                    fileDir ?: "",
                                    filename,
                                    byteStream
                                )
                                if (toDownloads) {
                                    dialog.context.let {
                                        if (filePath != null) {
                                            Utils.copyFileToDownloads(
                                                dialog.requireContext(),
                                                File(filePath),
                                                filename
                                            )
                                        }
                                    }
                                }

                                dialog.onFileSaved(filePath)
                            } catch (ex: Exception) {
                                dialog.onFileSaveFailed()
                            }
                        }.start()
                    }
                }
            }
        })
    }
}