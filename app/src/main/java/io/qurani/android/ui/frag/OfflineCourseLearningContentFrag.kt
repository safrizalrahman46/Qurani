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
import io.qurani.android.model.*
import io.qurani.android.model.view.ChapterView
import io.qurani.android.model.view.CourseCommonItem

class OfflineCourseLearningContentFrag : Fragment() {

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

        val chapters = ArrayList<ChapterCommonFields>()
        chapters.addAll(course.sessionChapters)
        chapters.addAll(course.filesChapters)
        chapters.addAll(course.textLessonChapters)

        val items = ArrayList<ChapterView>()

        for (chapter in chapters) {

            val innerItems: List<CourseCommonItem>
            val size: Int

            when (chapter) {
                is SessionChapter -> {
                    size = chapter.sessions.size
                    innerItems = chapter.sessions
                }
                is FileChapter -> {
                    size = chapter.files.size
                    innerItems = chapter.files.toMutableList()
                }
                is TextChapter -> {
                    size = chapter.textLessons.size
                    innerItems = chapter.textLessons.toMutableList()
                }

                else -> {
                    return
                }
            }

            items.add(
                ChapterView(
                    chapter.title,
                    "$size ${getString(R.string.lessons)}",
                    innerItems
                )
            )
        }

        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = ChaptersContentAdapter(items, course, requireActivity(), true)
    }
}
