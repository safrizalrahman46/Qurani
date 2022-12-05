package io.qurani.android.ui.frag.course

import io.qurani.android.model.Course

class CourseDetailsFactory {
    companion object {
        fun getDetails(course: Course): BaseCourseDetails {
            if (course.isBundle()) {
                return BundleCourseDetails(course)
            }

            return NormalCourseDetails(course)
        }
    }
}