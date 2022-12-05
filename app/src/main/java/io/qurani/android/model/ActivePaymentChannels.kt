package io.qurani.android.model

import com.google.gson.annotations.SerializedName

class ActivePaymentChannels {

    @SerializedName("active")
    lateinit var paymentChannels: List<PaymentChannel>
}