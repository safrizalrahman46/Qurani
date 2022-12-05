package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.CourseCommon2RvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.model.Course
import io.qurani.android.model.Quiz
import io.qurani.android.ui.MainActivity

class CourseLearningQuizzesFrag : Fragment(),
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
        val course = requireArguments().getParcelable<Course>(io.qurani.android.manager.App.COURSE)!!
        val offlineMode = requireArguments().getBoolean(io.qurani.android.manager.App.OFFLINE)
        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = CourseCommon2RvAdapter(course.quizzes)
        if (!offlineMode) {
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val quiz = (mBinding.rv.adapter as CourseCommon2RvAdapter).items[position] as Quiz

        val frag: Fragment
        val bundle = Bundle()

        frag = if (quiz.authStatus == Quiz.NOT_PARTICIPATED) {
            bundle.putParcelable(io.qurani.android.manager.App.QUIZ, quiz)
            QuizOverviewFrag()
        } else {
            bundle.putInt(io.qurani.android.manager.App.ID, quiz.id)
            QuizResultInfoFrag()
        }

        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }
}