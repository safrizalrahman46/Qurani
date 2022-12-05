package io.qurani.android.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.qurani.android.R
import io.qurani.android.databinding.FragSettingsSecurityBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.BuildVars
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.db.AppDb
import io.qurani.android.manager.net.ApiService
import io.qurani.android.model.ChangePassword
import io.qurani.android.model.Data
import io.qurani.android.model.Response
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.SettingsSecurityPresenterImpl

class SettingsSecurityFrag : Fragment(), SettingsFrag.SaveCallback {

    private lateinit var mBinding: FragSettingsSecurityBinding
    private lateinit var mPresenter: Presenter.SettingsSecurityPresenter

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableSaveBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun enableDisableSaveBtn() {
        val currentPassword = mBinding.settingsSecurityCurrentPassEdtx.text.toString()
        val newPassword = mBinding.settingsSecurityNewPassEdtx.text.toString()
        val retypeNewPassword = mBinding.settingsSecurityRetypeNewPassEdtx.text.toString()

        val enable = (currentPassword.isNotEmpty() && newPassword.isNotEmpty()
                && retypeNewPassword.isNotEmpty() && newPassword == retypeNewPassword)

        (parentFragment as SettingsFrag).changeSaveBtnEnable(enable)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSettingsSecurityBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if (BuildVars.LOGS_ENABLED) {
            Log.d(SettingsSecurityFrag::class.java.name, "init")
        }

        mPresenter = SettingsSecurityPresenterImpl(this)

        mBinding.settingsSecurityCurrentPassEdtx.addTextChangedListener(mTextWatcher)
        mBinding.settingsSecurityNewPassEdtx.addTextChangedListener(mTextWatcher)
        mBinding.settingsSecurityRetypeNewPassEdtx.addTextChangedListener(mTextWatcher)
    }

    fun onPasswordChanges(response: Data<io.qurani.android.model.Response>) {
        if (context == null) return

        if (response.isSuccessful) {
            val token = response.data!!.token
            io.qurani.android.manager.App.saveToLocal(token, requireContext(), AppDb.DataType.TOKEN)
            ApiService.createAuthorizedApiService(token)

            mBinding.settingsSecurityCurrentPassEdtx.setText("")
            mBinding.settingsSecurityNewPassEdtx.setText("")
            mBinding.settingsSecurityRetypeNewPassEdtx.setText("")
            ToastMaker.show(
                requireContext(),
                getString(R.string.success),
                response.message,
                ToastMaker.Type.SUCCESS
            )
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    override fun onSaveClicked() {
        val currentPassword = mBinding.settingsSecurityCurrentPassEdtx.text.toString()
        val newPassword = mBinding.settingsSecurityNewPassEdtx.text.toString()

        val changePassword = ChangePassword()
        changePassword.currentPassword = currentPassword
        changePassword.newPassword = newPassword

        mPresenter.changePassword(changePassword)
    }

    override fun initTab() {
        (parentFragment as SettingsFrag).changeSaveBtnEnable(false)
        (parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
    }
}