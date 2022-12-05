package io.qurani.android.ui.frag.course

import io.qurani.android.model.Course

class PricingPlanFactory {
    companion object {
        fun getPlan(course: Course): BasePricingPlan {
            if (course.isBundle()) {
                return BundlePricingPlan(course)
            }

            return NormalPricingPlan(course)
        }
    }
}