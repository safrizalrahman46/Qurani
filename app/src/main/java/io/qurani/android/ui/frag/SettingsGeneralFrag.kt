package io.qurani.android.ui.frag

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import io.qurani.android.R
import io.qurani.android.databinding.FragSettingsGeneralBinding
import io.qurani.android.manager.*
import io.qurani.android.manager.Utils.toBoolean
import io.qurani.android.manager.Utils.toInt
import io.qurani.android.manager.db.AppDb
import io.qurani.android.manager.net.ApiService
import io.qurani.android.model.*
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.SettingsGeneralPresenterImpl
import io.qurani.android.ui.BaseActivity
import io.qurani.android.ui.MainActivity


class SettingsGeneralFrag : Fragment(), View.OnClickListener,
    SelectionDialog.ItemSelection<Language>, SettingsFrag.SaveCallback {

    private lateinit var mBinding: FragSettingsGeneralBinding
    private lateinit var mPresenter: Presenter.SettingsGeneralPresenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSettingsGeneralBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = SettingsGeneralPresenterImpl(this)

        val user = io.qurani.android.manager.App.loggedInUser!!
        mBinding.settingsUserNameEdtx.setText(user.name)

        val prefManager = PrefManager(requireContext())
        mBinding.settingsUserLngTv.text = prefManager.language!!.name

        if (user.referral != null) {
            mBinding.settingsUserReferralTv.text = user.referral
            setReferralCopyListener(user.referral!!)
        } else {
            mBinding.settingsUserReferralKeyTv.visibility = View.GONE
            mBinding.settingsUserReferralTv.visibility = View.GONE
        }

        if (user.email != null) {
            mBinding.settingsUserEmailTv.text = user.email
        } else {
            mBinding.settingsUserEmailKeyTv.visibility = View.GONE
            mBinding.settingsUserEmailTv.visibility = View.GONE
        }

        if (user.mobile != null) {
            mBinding.settingsUserPhoneNumberTv.text = user.mobile
        } else {
            mBinding.settingsUserPhoneNumberKeyTv.visibility = View.GONE
            mBinding.settingsUserPhoneNumberTv.visibility = View.GONE
        }

        mBinding.settingsJoinNewsletterSwitch.isChecked = user.hasNewsLetter
        mBinding.settingsProfileMessagesSwitch.isChecked = user.publicMessage.toBoolean()
        mBinding.settingsUserLngTv.setOnClickListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setReferralCopyListener(referral: String) {
        mBinding.settingsUserReferralTv.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if ((mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_RIGHT] != null && event.rawX >= mBinding.settingsUserReferralTv.right - mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) ||
                    (mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_LEFT] != null && event.rawX <= (mBinding.settingsUserReferralTv.compoundDrawables[DRAWABLE_LEFT].bounds.width()))
                ) {
                    Utils.copyToClipbaord(
                        requireContext(),
                        getString(R.string.referral_url),
                        referral
                    )
                    return@OnTouchListener true
                }
            }
            v?.performClick()
            false
        })
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.settingsUserLngTv -> {
                val bundle = Bundle()
                bundle.putSerializable(io.qurani.android.manager.App.SELECTION_TYPE, SelectionDialog.Selection.Language)

                val instance = SelectionDialog.getInstance<Language>()
                instance.setOnItemSelected(this)
                instance.arguments = bundle
                instance.show(childFragmentManager, null)
            }
        }
    }

    override fun onItemSelected(language: Language) {
        if (BaseActivity.language!!.code != language.code) {
            mBinding.settingsUserLngTv.text = language.name

            val prefManager = PrefManager(requireContext())
            prefManager.language = language
            BaseActivity.language = language

            ApiService.createApiServiceWithLocale(prefManager.language!!.code)

            startActivity(Intent(requireContext(), MainActivity::class.java))
            activity?.finish()
        }
    }

    override fun onSaveClicked() {
        val name = mBinding.settingsUserNameEdtx.text.toString()
        if (name.isEmpty()) {
            mBinding.settingsUserNameEdtx.error = ""
            return
        }

        val changeSettings = UserChangeSettings()
        changeSettings.name = name
        changeSettings.hasNewsLetter = mBinding.settingsJoinNewsletterSwitch.isChecked
        changeSettings.publicMessage = mBinding.settingsProfileMessagesSwitch.isChecked.toInt()

        mPresenter.changeProfileSettings(changeSettings)
    }

    override fun initTab() {
        (parentFragment as SettingsFrag).changeSaveBtnEnable(true)
        (parentFragment as SettingsFrag).changeSaveBtnVisibility(true)
    }

    fun onSettingsChanged(
        response: io.qurani.android.model.BaseResponse,
        changeSettings: UserChangeSettings
    ) {
        if (context == null) return

        if (response.isSuccessful) {
            val loggedInUser = io.qurani.android.manager.App.loggedInUser!!
            loggedInUser.name = changeSettings.name
            loggedInUser.hasNewsLetter = changeSettings.hasNewsLetter
            loggedInUser.publicMessage = changeSettings.publicMessage
            io.qurani.android.manager.App.loggedInUser = loggedInUser
            io.qurani.android.manager.App.saveToLocal(
                Gson().toJson(loggedInUser, UserProfile::class.java),
                requireContext(),
                AppDb.DataType.USER
            )

            (activity as MainActivity).initUserInfo()

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
}