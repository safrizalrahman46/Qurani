package io.qurani.android.model

import com.google.gson.annotations.SerializedName

class Data<T> : io.qurani.android.model.BaseResponse() {
    @SerializedName(
        "data",
        alternate = ["user", "result", "cart", "order", "amounts", "quizzes", "teachers",
            "users", "webinars", "bundles", "bundle", "certificates", "answers", "assignments"]
    )
    var data: T? = null
}