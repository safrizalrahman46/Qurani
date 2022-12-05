package io.qurani.android.model

import com.google.gson.annotations.SerializedName

class Login {
    @SerializedName("username")
    lateinit var username: String

    @SerializedName("password")
    lateinit var password: String
}