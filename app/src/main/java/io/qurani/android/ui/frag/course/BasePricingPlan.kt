package io.qurani.android.ui.frag.course

import io.qurani.android.model.AddToCart
import io.qurani.android.model.PricingPlan

abstract class BasePricingPlan {
    abstract fun getAddToCartItem(plan: PricingPlan?) : AddToCart
}