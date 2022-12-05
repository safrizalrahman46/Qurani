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
import io.qurani.android.manager.adapter.AssignmentsRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Assignment
import io.qurani.android.presenterImpl.MyAssignmentsPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState

class MyAssignmentsFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState,
    io.qurani.android.manager.listener.OnItemClickListener {

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
        val presenter = MyAssignmentsPresenterImpl(this)
        presenter.getMyAssignments()
    }

    fun onAssignmentsReceived(assignments: List<Assignment>) {
        mBinding.rvProgressBar.visibility = View.GONE

        if (assignments.isEmpty()) {
            showEmptyState()
        } else {
            val rv = mBinding.rv
            rv.layoutManager = LinearLayoutManager(requireContext())
            rv.adapter = AssignmentsRvAdapter(assignments)
            rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    rv,
                    this
                )
            )
        }
    }

    private fun showEmptyState() {
        showEmptyState(
            R.drawable.no_submission,
            R.string.no_assignments,
            R.string.no_assignments_student_desc
        )
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val assignment = (mBinding.rv.adapter as AssignmentsRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.ITEM, assignment)

        val overviewFrag = AssignmentOverviewFrag()
        overviewFrag.arguments = bundle
        (activity as MainActivity).transact(overviewFrag)
    }
}