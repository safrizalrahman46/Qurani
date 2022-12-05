package io.qurani.android.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import io.qurani.android.R
import io.qurani.android.databinding.DialogUserRegistrationBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.db.AppDb
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import io.qurani.android.presenterImpl.UserRegistrationPresenterImpl
import io.qurani.android.ui.MainActivity


class UserRegistrationDialog : io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog(), View.OnClickListener {

    //    private lateinit var mToken: String
    private lateinit var mPresenter: Presenter.UserRegistrationPresenter
    private lateinit var mBinding: DialogUserRegistrationBinding
    private var mUser: ThirdPartyLogin? = null
    private var mUserId = 0

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableContinueBtn()
        }
    }

    private fun enableDisableContinueBtn() {
        val name = mBinding.userRegistrationNameEdtx.text.toString()
        val continueBtn = mBinding.userRegistrationContinueBtn

        if (name.isNotEmpty()) {
            if (!continueBtn.isEnabled) {
                continueBtn.isEnabled = true
            }

        } else if (continueBtn.isEnabled) {
            continueBtn.isEnabled = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogUserRegistrationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = UserRegistrationPresenterImpl(this)

        mBinding.userRegistrationNameEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.userRegistrationContinueBtn.setOnClickListener(this)

        mUser = requireArguments().getParcelable(io.qurani.android.manager.App.USER)
        mUserId = requireArguments().getInt(io.qurani.android.manager.App.USER_ID)

        mBinding.userRegistrationNameEdtx.requestFocus()

        if (mUser != null) {
            mBinding.userRegistrationNameEdtx.setText(mUser!!.name)
            mBinding.userRegistrationNameEdtx.setSelection(mUser!!.name.length)
            mUserId = mUser!!.userId
        }
//        Utils.showKeyboard(requireContext())
    }

    override fun onClick(v: View) {
        val name = mBinding.userRegistrationNameEdtx.text.toString()
        val referralCode = mBinding.userRegistrationReferralEdtx.text.toString()

        if (name.isEmpty()) {
            mBinding.userRegistrationNameEdtx.error = ""
            return
        }

        val user = User()
        user.userId = mUserId
        user.name = name
        if (referralCode.isNotEmpty()) {
            user.referral = referralCode
        }

        mPresenter.register(user)
    }

    fun onErrorOccured(error: io.qurani.android.model.BaseResponse) {
        ToastMaker.show(
            requireContext(),
            getString(R.string.error),
            error.message,
            ToastMaker.Type.ERROR
        )
    }

    fun onRegistrationSaved(
        response: Data<io.qurani.android.model.Response>,
        user: User
    ) {
        if (response.isSuccessful) {
            val commonPresenter = CommonApiPresenterImpl.getInstance()
            commonPresenter.getUserInfo(user.userId, object : ItemCallback<UserProfile> {
                override fun onItem(item: UserProfile, vararg args: Any) {
                    io.qurani.android.manager.App.saveToLocal(
                        Gson().toJson(item),
                        requireContext(),
                        AppDb.DataType.USER
                    )
                    io.qurani.android.manager.App.saveToLocal(response.data!!.token, requireContext(), AppDb.DataType.TOKEN)
                    io.qurani.android.manager.App.loggedInUser = item
                    ApiService.createAuthorizedApiService(response.data!!.token)
                    (activity as MainActivity).onUserRegistered()
                    dismiss()
                }
            })
        } else {
            onErrorOccured(response)
        }
    }
}