package io.qurani.android.presenterImpl

import io.qurani.android.manager.App
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.widget.NewTicketDialog
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class NewTicketPresenterImpl(private val dialog: NewTicketDialog) : Presenter.NewTicketPresenter {

    override fun getDepartments() {
        val departmentsReq = ApiService.apiClient!!.getDepartments()
        dialog.addNetworkRequest(departmentsReq)
        departmentsReq.enqueue(object : CustomCallback<List<Department>> {
            override fun onStateChanged(): io.qurani.android.manager.net.RetryListener {
                return io.qurani.android.manager.net.RetryListener {
                    getDepartments()
                }
            }

            override fun onResponse(
                call: Call<List<Department>>,
                response: Response<List<Department>>
            ) {
                if (response.body() != null) {
                    dialog.onDepartmentsReceived(response.body()!!)
                }
            }

        })
    }

    override fun addTicket(ticket: Ticket, file: File?) {
        if (file == null) {
            ApiService.apiClient!!.addTicket(ticket).enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        addTicket(ticket, file)
                    }
                }

                override fun onResponse(
                    call: Call<io.qurani.android.model.BaseResponse>,
                    response: Response<io.qurani.android.model.BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onTicketSaved(ticket, response.body()!!)
                    }
                }

                override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onRequestFailed()
                }

            })
        } else {
            val fileBody = file.asRequestBody()
            val filePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("attach", file.name, fileBody)

            ApiService.apiClient!!.addTicket(
                filePart,
                MultipartBody.Part.createFormData("title", ticket.title),
                MultipartBody.Part.createFormData("message", ticket.message!!),
                MultipartBody.Part.createFormData("type", ticket.type!!),
                MultipartBody.Part.createFormData("department_id", ticket.departmentId!!.toString())
            ).enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        addTicket(ticket, file)
                    }
                }

                override fun onResponse(
                    call: Call<Data<io.qurani.android.model.Response>>,
                    response: Response<Data<io.qurani.android.model.Response>>
                ) {
                    if (response.body() != null) {
                        dialog.onTicketSaved(ticket, response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Data<io.qurani.android.model.Response>>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onRequestFailed()
                }

            })
        }
    }

    override fun addTicketChat(conversation: Conversation, file: File?) {
        if (file == null) {
            ApiService.apiClient!!.addTicketConverstation(conversation.id, conversation)
                .enqueue(object : CustomCallback<io.qurani.android.model.BaseResponse> {
                    override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                        return io.qurani.android.manager.net.RetryListener {
                            addTicketChat(conversation, file)
                        }
                    }

                    override fun onResponse(
                        call: Call<io.qurani.android.model.BaseResponse>,
                        response: Response<io.qurani.android.model.BaseResponse>
                    ) {
                        if (response.body() != null) {
                            dialog.onTicketConversationSaved(conversation, response.body()!!)
                        }
                    }

                    override fun onFailure(call: Call<io.qurani.android.model.BaseResponse>, t: Throwable) {
                        super.onFailure(call, t)
                        dialog.onRequestFailed()
                    }

                })

        } else {
            val fileBody = file.asRequestBody()
            val filePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("attach", file.name, fileBody)

            ApiService.apiClient!!.addTicketConverstation(
                conversation.id,
                filePart,
                MultipartBody.Part.createFormData("message", conversation.message)
            )
                .enqueue(object : CustomCallback<Data<io.qurani.android.model.Response>> {
                    override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                        return io.qurani.android.manager.net.RetryListener {
                            addTicketChat(conversation, file)
                        }
                    }

                    override fun onResponse(
                        call: Call<Data<io.qurani.android.model.Response>>,
                        response: Response<Data<io.qurani.android.model.Response>>
                    ) {
                        if (response.body() != null) {
                            dialog.onTicketConversationSaved(conversation, response.body()!!)
                        }
                    }

                    override fun onFailure(call: Call<Data<io.qurani.android.model.Response>>, t: Throwable) {
                        super.onFailure(call, t)
                        dialog.onRequestFailed()
                    }

                })

        }
    }

    override fun getCourses() {
        if (io.qurani.android.manager.App.loggedInUser!!.isUser()) {
            val myPurchasesReq = ApiService.apiClient!!.getMyPurchases()
            dialog.addNetworkRequest(myPurchasesReq)
            myPurchasesReq.enqueue(object : CustomCallback<Data<Count<Course>>> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getCourses()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<Course>>>,
                    response: Response<Data<Count<Course>>>
                ) {
                    if (response.body() != null) {
                        dialog.onMyClassesReceived(response.body()!!.data!!.items)
                    }
                }

            })
        } else {
            val myClassesPageDataReq = ApiService.apiClient!!.getMyClassesPageData()
            dialog.addNetworkRequest(myClassesPageDataReq)
            myClassesPageDataReq.enqueue(object : CustomCallback<MyClasses> {
                override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                    return io.qurani.android.manager.net.RetryListener {
                        getCourses()
                    }
                }

                override fun onResponse(call: Call<MyClasses>, response: Response<MyClasses>) {
                    if (response.body() != null) {
                        dialog.onMyClassesReceived(response.body()!!.myClasses)
                    }
                }
            })
        }
    }
}