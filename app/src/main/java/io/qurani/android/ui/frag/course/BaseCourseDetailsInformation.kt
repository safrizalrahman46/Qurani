package io.qurani.android.ui.frag.course

import io.qurani.android.databinding.FragCourseDetailsInformationBinding
import io.qurani.android.model.view.CourseCommonItem

abstract class BaseCourseDetailsInformation() {
    abstract fun getInfoList(): ArrayList<CourseCommonItem>
    abstract fun setMarkInfo(binding: FragCourseDetailsInformationBinding)
}