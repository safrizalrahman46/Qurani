package io.qurani.android.manager.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import io.qurani.android.R
import io.qurani.android.databinding.ItemChapterBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.listener.ItemClickListener
import io.qurani.android.manager.listener.OnItemClickListener
import io.qurani.android.model.*
import io.qurani.android.model.view.ContentItem
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.frag.*

class ChapterRvAdapter(
    items: List<ContentItem>,
    private val course: Course,
    private val activity: MainActivity
) :
    io.qurani.android.manager.adapter.BaseArrayAdapter<ContentItem, ChapterRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        viewHolder.binding.itemChapterTitleTv.text = item.title
        viewHolder.binding.itemChapterRv.adapter =
            ChapterItemRvAdapter(item.chapterItems, activity.windowManager)
    }

    inner class ViewHolder(val binding: ItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root),
        io.qurani.android.manager.listener.OnItemClickListener {
        init {
            binding.itemChapterRv.addOnItemTouchListener(
                io.qurani.android.manager.listener.ItemClickListener(
                    binding.itemChapterRv,
                    this
                )
            )
        }

        override fun onClick(view: View?, position: Int, id: Int) {
            val item = items[bindingAdapterPosition].chapterItems[position]
            val nextFrag: Fragment
            val bundle = Bundle()

            if (!course.hasUserBought && !item.can.view) {
                showBuyAlert()
                return
            }

            when {
                item.isQuiz() -> {
                    if (course.hasUserBought) {
                        if (item.authStatus == Quiz.NOT_PARTICIPATED) {
                            bundle.putSerializable(io.qurani.android.manager.App.TYPE, QuizzesFrag.Type.NOT_PARTICIPATED)
                        } else {
                            bundle.putSerializable(io.qurani.android.manager.App.TYPE, QuizzesFrag.Type.MY_RESULT)
                        }
                    } else {
                        bundle.putSerializable(io.qurani.android.manager.App.TYPE, QuizzesFrag.Type.NOT_PARTICIPATED)
                    }

                    nextFrag = QuizzesTabFrag()
                }
                item.isCert() -> {
                    nextFrag = CertificatesTabFrag()
                }
                item.isAssignment() -> {
                    nextFrag = AssignmentsTabFrag()
                }
                else -> {
                    bundle.putParcelable(io.qurani.android.manager.App.COURSE, course)
                    bundle.putParcelable(io.qurani.android.manager.App.ITEM, item)
                    nextFrag = CourseChapterItemFrag()
                }
            }

            nextFrag.arguments = bundle
            activity.transact(nextFrag)
        }

        private fun showBuyAlert() {
            ToastMaker.show(
                itemView.context,
                itemView.context.getString(R.string.error),
                itemView.context.getString(R.string.you_have_to_buy_this_course),
                ToastMaker.Type.ERROR
            )
        }

        override fun onLongClick(view: View?, position: Int, id: Int) {
        }
    }
}