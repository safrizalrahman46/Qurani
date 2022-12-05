package io.qurani.android.ui.frag.course

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.qurani.android.manager.adapter.ViewPagerAdapter
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.model.AddToCart
import io.qurani.android.model.AddToFav
import io.qurani.android.model.Course

abstract class BaseCourseAddToFav {
    abstract fun getAddToFavItem(): AddToFav
}