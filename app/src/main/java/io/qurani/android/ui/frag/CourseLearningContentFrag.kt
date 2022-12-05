package io.qurani.android.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.qurani.android.R
import io.qurani.android.databinding.RvBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ChaptersContentAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.model.*
import io.qurani.android.model.view.ChapterView
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import java.util.ArrayList

class CourseLearningContentFrag : Fragment(), ItemCallback<List<Chapter>> {
    private lateinit var mBinding: RvBinding
    private lateinit var mCourse: Course

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
        mCourse = requireArguments().getParcelable(io.qurani.android.manager.App.COURSE)!!

        val presenter = CommonApiPresenterImpl.getInstance()
        presenter.getCourseContent(mCourse.id, this)
    }

    override fun onItem(chapters: List<Chapter>, vararg args: Any) {
        if (context == null) return

        val items = ArrayList<ChapterView>()

        for (chapter in chapters) {
            items.add(
                ChapterView(
                    chapter.title,
                    "${chapter.items.size} ${getString(R.string.lessons)}",
                    chapter.items
                )
            )
        }

        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = ChaptersContentAdapter(
            items, mCourse, requireActivity(), false
        )
    }
}