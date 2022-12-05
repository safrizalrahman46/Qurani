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
import io.qurani.android.manager.adapter.AssignmentStudentRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Assignment
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class AssignmentStudentsFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(),
    io.qurani.android.manager.listener.OnItemClickListener, EmptyState {

    private lateinit var mBinding: RvBinding
    private var mIsInstructorType = false

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
        val assignments = requireArguments().getParcelableArrayList<Assignment>(io.qurani.android.manager.App.ITEMS)!!
        mIsInstructorType = requireArguments().getBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE)

        mBinding.rvProgressBar.visibility = View.GONE
        if (assignments.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = AssignmentStudentRvAdapter(assignments)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        } else {
            showEmptyState()
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val assignment = ((mBinding.rv.adapter) as AssignmentStudentRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.ITEM, assignment)
        bundle.putBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE, mIsInstructorType)

        val frag = AssignmentConversationFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        showEmptyState(
            R.drawable.no_submission,
            R.string.no_submissions,
            R.string.no_submissions_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}