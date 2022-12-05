package io.qurani.android.manager

import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Data
import io.qurani.android.model.Response
import io.qurani.android.model.ThirdPartyLogin

interface ThirdPartyCallback {
    fun onThirdPartyLogin(res: Data<io.qurani.android.model.Response>, provider: Int, thirdPartyLogin: ThirdPartyLogin)
    fun onErrorOccured(error: io.qurani.android.model.BaseResponse)
}