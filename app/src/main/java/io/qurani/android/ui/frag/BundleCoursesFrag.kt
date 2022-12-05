package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ClassListGridRvAdapter
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Course
import io.qurani.android.presenterImpl.BundleCoursesPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class BundleCoursesFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(io.qurani.android.manager.App.COURSE)!!

        val presenter = BundleCoursesPresenterImpl(this)
        presenter.getBundleCourses(course.id)
    }

    fun onCoursesReceived(items: List<Course>) {
        mBinding.rvProgressBar.visibility = View.GONE

        if (items.isNotEmpty()) {
            val rv = mBinding.rv
            val layoutManager = LinearLayoutManager(requireContext())
            rv.layoutManager = layoutManager

            rv.adapter = ClassListGridRvAdapter(
                items, activity as MainActivity, layoutManager
            )
        } else {
            showEmptyState()
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_course, R.string.no_courses, R.string.no_courses_found)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}