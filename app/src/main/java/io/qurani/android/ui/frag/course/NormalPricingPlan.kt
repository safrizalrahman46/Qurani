package io.qurani.android.ui.frag.course

import io.qurani.android.model.AddToCart
import io.qurani.android.model.Course
import io.qurani.android.model.PricingPlan

class NormalPricingPlan(val course: Course) : BasePricingPlan() {

    override fun getAddToCartItem(plan: PricingPlan?): AddToCart {
        val addToCart = AddToCart()
        plan?.let { addToCart.pricingPlanId = it.id }
        addToCart.itemId = course.id
        addToCart.itemName = AddToCart.ItemType.WEBINAR.value
        return addToCart
    }

}