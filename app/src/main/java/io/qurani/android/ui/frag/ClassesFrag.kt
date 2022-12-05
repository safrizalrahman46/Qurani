package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.Utils
import io.qurani.android.manager.adapter.ClassListGridRvAdapter
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Course
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.model.view.EmptyStateData
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.ClassesPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class ClassesFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState {
    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.ClassesPresenter
    private var mTitle: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val classes = requireArguments().getParcelableArrayList<Course>(io.qurani.android.manager.App.COURSES)
        val useGrid = requireArguments().getBoolean(io.qurani.android.manager.App.USE_GRID)
        val nestedEnabled = requireArguments().getBoolean(io.qurani.android.manager.App.NESTED_ENABLED)

        mTitle = requireArguments().getString(io.qurani.android.manager.App.TITLE)
        val emptyStateData = requireArguments().getParcelable<EmptyStateData>(io.qurani.android.manager.App.EMPTY_STATE)

        if (mTitle != null) {
            val toolbarOptions = ToolbarOptions()
            toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

            (activity as MainActivity).showToolbar(toolbarOptions, mTitle!!)
        }

        if (nestedEnabled) {
            mBinding.rv.isNestedScrollingEnabled = true
        }

        if (useGrid) {
            mBinding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        }

        if (classes != null) {
            mBinding.rvProgressBar.visibility = View.GONE
            if (classes.isEmpty()) {
                showEmptyState(emptyStateData!!)
                return
            }

            if (useGrid) {
                val params = mBinding.rv.layoutParams as FrameLayout.LayoutParams
                params.marginStart = Utils.changeDpToPx(requireContext(), 16f).toInt()
                params.marginEnd = Utils.changeDpToPx(requireContext(), 16f).toInt()
                mBinding.rv.requestLayout()

                val adapter = ClassListGridRvAdapter(
                    classes, activity as MainActivity,
                    mBinding.rv.layoutManager as GridLayoutManager
                )
                mBinding.rv.adapter = adapter
            } else {
                val adapter = ClassListGridRvAdapter(classes, activity as MainActivity)
                mBinding.rv.adapter = adapter
            }
        } else {
            mBinding.rvProgressBar.visibility = View.GONE
            val map = requireArguments().getSerializable(io.qurani.android.manager.App.FILTERS) as HashMap<String, String>
            mPresenter = ClassesPresenterImpl(
                this,
                mBinding.rv,
                map
            )

            mPresenter.getCourses()
        }
    }

    fun showEmptyStateForCourse() {
        showEmptyState(
            R.drawable.no_course,
            getString(R.string.no_courses),
            "${getString(R.string.no)} $mTitle ${getString(R.string.found)}"
        )
    }

    fun showEmptyState(emptyStateData: EmptyStateData) {
        showEmptyState(emptyStateData.img, emptyStateData.titleRes, emptyStateData.descRes)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}