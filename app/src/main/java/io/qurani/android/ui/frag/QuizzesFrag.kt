package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.QuizResultRvAdapter
import io.qurani.android.manager.adapter.QuizRvAdapter
import io.qurani.android.manager.adapter.QuizStudentResultRvAdapter
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.Quiz
import io.qurani.android.model.QuizResult
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.QuzziesPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState
import java.io.Serializable

class QuizzesFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(),
    io.qurani.android.manager.listener.OnItemClickListener, EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.QuzziesPresenter
    private lateinit var mType: Type


    enum class Type : Serializable {
        MY_RESULT, NOT_PARTICIPATED, STUDENT_RESULT, LIST
    }

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
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        mType = requireArguments().getSerializable(io.qurani.android.manager.App.SELECTION_TYPE) as Type

        mPresenter = QuzziesPresenterImpl(this)

        when (mType) {
            Type.MY_RESULT -> mPresenter.getMyResults()
            Type.NOT_PARTICIPATED -> mPresenter.getNotParticipated()
            Type.STUDENT_RESULT -> mPresenter.getStudentResults()
            Type.LIST -> mPresenter.getQuizList()
        }
    }

    fun onQuizzesResultRecevied(results: List<QuizResult>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (results.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = QuizResultRvAdapter(results)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        } else {
            showMyResultEmptyState(R.string.no_quiz_result_desc)
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val frag: Fragment

        when (mType) {
            Type.MY_RESULT -> {
                val quizResult = (mBinding.rv.adapter as QuizResultRvAdapter).items[position]

                val bundle = Bundle()
                bundle.putParcelable(io.qurani.android.manager.App.RESULT, quizResult)

                frag = QuizResultInfoFrag()
                frag.arguments = bundle
            }
            Type.NOT_PARTICIPATED -> {
                val quiz = (mBinding.rv.adapter as QuizRvAdapter).items[position]

                val bundle = Bundle()
                bundle.putParcelable(io.qurani.android.manager.App.QUIZ, quiz)

                frag = QuizOverviewFrag()
                frag.arguments = bundle
            }
            Type.STUDENT_RESULT -> {
                val quizResult = (mBinding.rv.adapter as QuizStudentResultRvAdapter).items[position]

                val bundle = Bundle()
                bundle.putParcelable(io.qurani.android.manager.App.RESULT, quizResult)
                bundle.putBoolean(io.qurani.android.manager.App.INSTRUCTOR_TYPE, true)

                frag = QuizResultInfoFrag()
                frag.arguments = bundle

            }
            else -> {
                val quiz = (mBinding.rv.adapter as QuizRvAdapter).items[position]

                val bundle = Bundle()
                bundle.putParcelable(io.qurani.android.manager.App.QUIZ, quiz)

                frag = QuizResultInfoFrag()
                frag.arguments = bundle
            }
        }

        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun onQuizListRecevied(items: List<Quiz>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = QuizRvAdapter(items)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        } else {
            if (mType == Type.NOT_PARTICIPATED) {
                showMyResultEmptyState(R.string.you_have_particapted_in_all)
            } else {
                showMyResultEmptyState(R.string.you_donnot_have_quiz)
            }
        }
    }

    fun onStudentResultRecevied(items: List<QuizResult>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = QuizStudentResultRvAdapter(items)
            mBinding.rv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    mBinding.rv,
                    this
                )
            )
        } else {
            showStudentResultEmptyState()
        }
    }

    fun showStudentResultEmptyState() {
        showEmptyState(R.drawable.no_biography, R.string.no_student_result, R.string.no_student_result_desc)
    }

    fun showMyResultEmptyState(@StringRes desc: Int) {
        showEmptyState(R.drawable.no_biography, R.string.no_result, desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}