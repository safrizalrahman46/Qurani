package io.qurani.android.ui.frag.course

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.model.AddToCart
import io.qurani.android.model.Course
import io.qurani.android.model.Review

abstract class BaseCourseDetails {
    abstract fun getToolbarTitle(): Int
    abstract fun getTabsAdapter(
        context: Context,
        fragmentManager: FragmentManager,
    ): io.qurani.android.manager.adapter.ViewPagerAdapter
    abstract fun getCourseDetails(
        id: Int,
        callback: ItemCallback<Course>
    )
    abstract fun getAddToCartItem() : AddToCart
    abstract fun getBaseReviewObj() : Review
    abstract fun getCourseType(): String
}