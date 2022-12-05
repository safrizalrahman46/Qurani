package io.qurani.android.ui.frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.EmptyStateBinding
import io.qurani.android.databinding.FragForumsBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.CardStatisticsRvAdapter
import io.qurani.android.manager.adapter.ForumItemRvAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.model.view.CommonItem
import io.qurani.android.presenterImpl.ForumsPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.abs.EmptyState
import io.qurani.android.ui.widget.ForumQuestionDialog
import io.qurani.android.ui.widget.ForumSearchDialog
import java.lang.StringBuilder

class ForumsFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), EmptyState, View.OnClickListener,
    ItemCallback<String> {

    private lateinit var mBinding: FragForumsBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: ForumsPresenterImpl
    private var mSearch: String? = null

    private val mCallback = object : ItemCallback<Any> {
        override fun onItem(item: Any, vararg args: Any) {
            val forumsRvAdapter = mBinding.forumsRv.adapter as ForumItemRvAdapter
            val size = forumsRvAdapter.items.size
            forumsRvAdapter.items.clear()
            forumsRvAdapter.notifyItemRangeRemoved(0, size)
            mBinding.forumsRvProgressBar.visibility = View.VISIBLE

            mPresenter.getForumQuestions(mCourse.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragForumsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(io.qurani.android.manager.App.COURSE)!!
        mSearch = requireArguments().getString(io.qurani.android.manager.App.KEY)

        initPresenter()
        initUI()
    }

    private fun initPresenter() {
        mPresenter = ForumsPresenterImpl(this)
        if (mSearch.isNullOrEmpty()) {
            mPresenter.getForumQuestions(mCourse.id)
        } else {
            hideStatistics()
            hideBtns()
            mPresenter.searchInCourseForum(mCourse.id, mSearch!!)
        }
    }

    private fun hideStatistics() {
        mBinding.forumsStatisticsRv.visibility = View.GONE
    }

    private fun hideBtns() {
        mBinding.forumsBtnsContainer.visibility = View.GONE
    }

    private fun initUI() {
        mBinding.forumsAskBtn.setOnClickListener(this)
        mBinding.forumsSearchBtn.setOnClickListener(this)
    }

    fun showEmptyState() {
        if (mSearch.isNullOrEmpty()) {
            showEmptyState(
                R.drawable.no_comments,
                R.string.no_questions,
                R.string.no_questions_forum
            )
        } else {
            showEmptyState(R.drawable.no_comments, getString(R.string.no_questions), "")
        }
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.forumsEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onForumReceived(forums: Forums) {
        mBinding.forumsRvProgressBar.visibility = View.GONE

        if (forums.items.isEmpty()) {
            showEmptyState()
            updateUINoQuestions()
            return
        }

        if (mSearch.isNullOrEmpty()) {
            initStatistics(forums)
        } else {
            setToolbarText(forums.items.size)
        }

        initRV(forums.items)
    }

    private fun initRV(items: List<ForumItem>) {
        mBinding.forumsRv.adapter = ForumItemRvAdapter(items, this, mCallback)
    }

    private fun setToolbarText(foundedItems: Int) {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        val builder = StringBuilder()
            .append(foundedItems)
            .append(" ")
            .append(getString(R.string.results_found_for))
            .append("\"")
            .append(mSearch)
            .append("\"")

        (activity as MainActivity).showToolbar(
            toolbarOptions,
            builder.toString()
        )
    }

    private fun updateUINoQuestions() {
        mBinding.forumsSearchBtn.visibility = View.GONE
        val params = mBinding.forumsAskBtn.layoutParams as ConstraintLayout.LayoutParams
        params.marginEnd = resources.getDimension(R.dimen.margin_16).toInt()
        mBinding.forumsAskBtn.requestLayout()
    }

    private fun initStatistics(forums: Forums) {
        val items = ArrayList<CommonItem>()
        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.questionsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.questions)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_questions_red
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_red
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.resolvedCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.resolved)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_done_green
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_green
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.openQuestionsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.open_questions)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_more_circle_blue
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_blue
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.commentsCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.answers)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_comments_light_green
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_green2
            }
        })

        items.add(object : CommonItem {
            override fun title(context: Context): String {
                return forums.activeUsersCount.toString()
            }

            override fun img(): String? {
                return null
            }

            override fun desc(context: Context): String {
                return getString(R.string.active_users)
            }

            override fun imgResource(): Int {
                return R.drawable.ic_user_dark_blue
            }

            override fun imgBgResource(): Int {
                return R.drawable.circle_light_dark_gray
            }
        })

        mBinding.forumsStatisticsRv.adapter = CardStatisticsRvAdapter(items)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.forums_search_btn -> {
                val dialog = ForumSearchDialog()
                dialog.setOnSearchListener(this)
                dialog.show(childFragmentManager, null)
            }

            R.id.forums_ask_btn -> {
                val bundle = Bundle()
                bundle.putInt(io.qurani.android.manager.App.ID, mCourse.id)

                val dialog = ForumQuestionDialog()
                dialog.arguments = bundle
                dialog.setCallback(mCallback)
                dialog.show(childFragmentManager, null)
            }
        }
    }

    override fun onItem(s: String, vararg args: Any) {
        val bundle = Bundle()
        bundle.putString(io.qurani.android.manager.App.KEY, s)
        bundle.putParcelable(io.qurani.android.manager.App.COURSE, mCourse)

        val searchForumFrag = ForumsFrag()
        searchForumFrag.arguments = bundle
        (activity as MainActivity).transact(searchForumFrag)
    }
}