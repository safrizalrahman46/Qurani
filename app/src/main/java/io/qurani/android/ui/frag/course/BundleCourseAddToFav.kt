package io.qurani.android.ui.frag.course

import io.qurani.android.model.AddToFav
import io.qurani.android.model.Course

class BundleCourseAddToFav(val course: Course) : BaseCourseAddToFav() {

    override fun getAddToFavItem(): AddToFav {
        val addToFav = AddToFav()
        addToFav.itemId = course.id
        addToFav.itemName = AddToFav.ItemType.BUNDLE.value
        return addToFav
    }
}