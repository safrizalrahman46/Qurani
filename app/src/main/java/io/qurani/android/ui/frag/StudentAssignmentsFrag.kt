package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragStudentAssignmentsBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.AssignmentsRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.StudentAssignments
import io.qurani.android.presenterImpl.StudentAssignmentsPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class StudentAssignmentsFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState,
    io.qurani.android.manager.listener.OnItemClickListener {

    private lateinit var mBinding: FragStudentAssignmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragStudentAssignmentsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val presenter = StudentAssignmentsPresenterImpl(this)
        presenter.getStudentAssignments()
    }

    private fun showEmptyState() {
        mBinding.studentAssignmentsPendingHeaderContainer.visibility = View.GONE

        showEmptyState(
            R.drawable.no_submission,
            R.string.no_assignments,
            R.string.no_assignments_teacher
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.studentAssignmentsEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onAssignmentsReceived(studentAssignments: StudentAssignments) {
        mBinding.studentAssignmentsRvProgressBar.visibility = View.GONE
        val assignments = studentAssignments.assignments

        if (assignments.isEmpty()) {
            showEmptyState()
        } else {
            mBinding.studentAssignmentsPendingTv.text =
                studentAssignments.pendingAssignments.toString()
            mBinding.studentAssignmentsPassedTv.text =
                studentAssignments.passedAssignments.toString()
            mBinding.studentAssignmentsFailedTv.text =
                studentAssignments.failedAssignments.toString()

            val rv = mBinding.studentAssignmentsRv

            rv.adapter = AssignmentsRvAdapter(assignments, true)
            rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    rv,
                    this
                )
            )
        }
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val assignment =
            ((mBinding.studentAssignmentsRv.adapter) as AssignmentsRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.ITEM, assignment)
        bundle.putBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE, true)

        val frag = AssignmentOverviewFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

}