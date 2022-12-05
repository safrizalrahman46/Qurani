package io.qurani.android.presenterImpl

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.EndLessClassListRvAdapter
import io.qurani.android.manager.adapter.EndLessLoadMoreAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.CustomCallback
import io.qurani.android.manager.net.RetryListener
import io.qurani.android.model.Count
import io.qurani.android.model.Course
import io.qurani.android.model.Data
import io.qurani.android.presenter.Presenter
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.ClassesFrag
import io.qurani.android.ui.frag.CourseDetailsFrag
import retrofit2.Call
import retrofit2.Response
import java.util.ArrayList

class ClassesPresenterImpl(
    private val frag: ClassesFrag,
    private val rv: RecyclerView,
    private val map: HashMap<String, String>
) :
    Presenter.ClassesPresenter {

    companion object {
        private const val PAGE_COUNT = 10
    }

    private val mItems: MutableList<Course?>
    private val mAdapter: EndLessClassListRvAdapter
    private var mOffset = 0

    init {
        mItems = ArrayList()
        mAdapter = EndLessClassListRvAdapter(mItems, frag.activity as MainActivity)
        rv.adapter = mAdapter
        mAdapter.setLoadMoreListener {
            getCourses()
        }
        mAdapter.setLoading(true)
    }

    override fun getCourses() {
        rv.post {
            mItems.add(null)
            mAdapter.notifyItemInserted(mItems.size - 1)

            map["offset"] = mOffset.toString()
            map["limit"] = PAGE_COUNT.toString()
            val courses = ApiService.apiClient!!.getCourses(map)
            frag.addNetworkRequest(courses)
            courses.enqueue(object : CustomCallback<Data<List<Course>>> {
                    override fun onStateChanged(): io.qurani.android.manager.net.RetryListener? {
                        return io.qurani.android.manager.net.RetryListener {
                            getCourses()
                        }
                    }

                    override fun onResponse(
                        call: Call<Data<List<Course>>>,
                        response: Response<Data<List<Course>>>
                    ) {
                        if (response.body() != null) {
                            val newItems = response.body()!!.data!!

                            mOffset += PAGE_COUNT
                            val index = mItems.size - 1
                            val adapter = rv.adapter as io.qurani.android.manager.adapter.EndLessLoadMoreAdapter<*, *>
                            mItems.removeAt(index)
                            adapter.notifyItemRemoved(index)
                            if (newItems.isNotEmpty()) {
                                mItems.addAll(newItems)
                                adapter.notifyItemRangeInserted(index, newItems.size)
                            } else {
                                adapter.isMoreDataAvailable = false
                                if (mItems.size == 0) {
                                    frag.showEmptyStateForCourse()
                                }
                            }
                            adapter.setLoading(false)
                        }
                    }
                })
        }
    }
}