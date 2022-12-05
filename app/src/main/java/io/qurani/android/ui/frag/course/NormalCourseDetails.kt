package io.qurani.android.ui.frag.course

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import io.qurani.android.R
import io.qurani.android.manager.App
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.model.AddToCart
import io.qurani.android.model.Course
import io.qurani.android.model.Review
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import io.qurani.android.ui.frag.CourseDetailsCommentsFrag
import io.qurani.android.ui.frag.CourseDetailsContentFrag
import io.qurani.android.ui.frag.CourseDetailsInformationFrag
import io.qurani.android.ui.frag.CourseDetailsReviewsFrag

class NormalCourseDetails(private val course: Course) : BaseCourseDetails() {

    override fun getToolbarTitle(): Int {
        return R.string.course_details
    }

    override fun getTabsAdapter(
        context: Context,
        fragmentManager: FragmentManager,
    ): io.qurani.android.manager.adapter.ViewPagerAdapter {

        val bundle = Bundle()
        bundle.putParcelable(io.qurani.android.manager.App.COURSE, course)

        val informationFrag = CourseDetailsInformationFrag()
        val contentFrag = CourseDetailsContentFrag()
        val reviewsFrag = CourseDetailsReviewsFrag()
        val commentsFrag = CourseDetailsCommentsFrag()

        informationFrag.arguments = bundle
        contentFrag.arguments = bundle
        reviewsFrag.arguments = bundle
        commentsFrag.arguments = bundle

        val adapter = io.qurani.android.manager.adapter.ViewPagerAdapter(fragmentManager)
        adapter.add(informationFrag, context.getString(R.string.information))
        adapter.add(contentFrag, context.getString(R.string.content))
        adapter.add(reviewsFrag, context.getString(R.string.reviews))
        adapter.add(commentsFrag, context.getString(R.string.comments))
        return adapter
    }

    override fun getCourseDetails(id: Int, callback: ItemCallback<Course>) {
        val presenter = CommonApiPresenterImpl.getInstance()
        presenter.getCourseDetails(id, callback)
    }

    override fun getAddToCartItem(): AddToCart {
        val addToCart = AddToCart()
        addToCart.itemId = course.id
        addToCart.itemName = AddToCart.ItemType.WEBINAR.value
        return addToCart
    }

    override fun getBaseReviewObj(): Review {
        val review = Review()
        review.id = course.id
        review.item = Course.Type.WEBINAR.value
        return review
    }

    override fun getCourseType(): String {
        return Course.Type.WEBINAR.value
    }
}