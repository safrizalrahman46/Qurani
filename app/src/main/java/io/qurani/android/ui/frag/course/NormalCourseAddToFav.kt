package io.qurani.android.ui.frag.course

import io.qurani.android.model.AddToFav
import io.qurani.android.model.Course

class NormalCourseAddToFav(val course: Course) : BaseCourseAddToFav() {

    override fun getAddToFavItem(): AddToFav {
        val addToFav = AddToFav()
        addToFav.itemId = course.id
        addToFav.itemName = AddToFav.ItemType.WEBINAR.value
        return addToFav
    }
}