package io.qurani.android.ui.frag.course

import io.qurani.android.model.Course

class CourseAddToFavFactory {
    companion object {
        fun getAddToFavObj(course: Course): BaseCourseAddToFav {
            if (course.isBundle()) {
                return BundleCourseAddToFav(course)
            }

            return NormalCourseAddToFav(course)
        }
    }
}