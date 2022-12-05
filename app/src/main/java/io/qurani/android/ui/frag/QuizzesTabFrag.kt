package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.TabBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.model.ToolbarOptions
import io.qurani.android.ui.MainActivity

class QuizzesTabFrag : Fragment() {

    private lateinit var mBinding: TabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.quizzes)

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val myResultsFrag = QuizzesFrag()
        val notParticipatedFrag = QuizzesFrag()

        var bundle = Bundle()
        bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, QuizzesFrag.Type.MY_RESULT)
        myResultsFrag.arguments = bundle

        bundle = Bundle()
        bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, QuizzesFrag.Type.NOT_PARTICIPATED)
        notParticipatedFrag.arguments = bundle

        val adapter = io.qurani.android.manager.adapter.ViewPagerAdapter(childFragmentManager)
        adapter.add(myResultsFrag, getString(R.string.my_results))
        adapter.add(notParticipatedFrag, getString(R.string.not_participated))

        if (io.qurani.android.manager.App.loggedInUser!!.isInstructor() || io.qurani.android.manager.App.loggedInUser!!.isOrganizaton()) {
            val studentsResultsFrag = QuizzesFrag()
            val listFrag = QuizzesFrag()

            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, QuizzesFrag.Type.STUDENT_RESULT)
            studentsResultsFrag.arguments = bundle

            bundle = Bundle()
            bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, QuizzesFrag.Type.LIST)
            listFrag.arguments = bundle

            adapter.add(studentsResultsFrag, getString(R.string.student_results))
            adapter.add(listFrag, getString(R.string._list))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        val serializable = arguments?.getSerializable(io.qurani.android.manager.App.TYPE)
        if (serializable != null){
            val type = serializable as QuizzesFrag.Type
            if (type == QuizzesFrag.Type.MY_RESULT) {
                viewPager.currentItem = 0
            }else if (type == QuizzesFrag.Type.NOT_PARTICIPATED) {
                viewPager.currentItem = 1
            }
        }
    }

}