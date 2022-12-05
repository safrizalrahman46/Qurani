package io.qurani.android.ui.widget

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.qurani.android.R
import io.qurani.android.databinding.DialogMeetingJoinDetailsBinding
import io.qurani.android.manager.App
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog
import io.qurani.android.model.BaseResponse
import io.qurani.android.model.Meeting
import io.qurani.android.model.ReserveMeeting
import io.qurani.android.presenter.Presenter
import io.qurani.android.presenterImpl.MeetingJoinDetailsPresenterImpl

class MeetingJoinDetailsDialog : io.qurani.android.manager.net.observer.NetworkObserverBottomSheetDialog(), View.OnClickListener {

    private lateinit var mBinding: DialogMeetingJoinDetailsBinding
    private lateinit var mMeeting: Meeting
    private lateinit var mPresenter: Presenter.MeetingJoinDetailsPresenter

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            enableDisableBtn()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    private fun enableDisableBtn() {
        val url = mBinding.meetingJoinDetailsUrlEdtx.text.toString()
        mBinding.meetingJoinDetailsSaveBtn.isEnabled = url.isNotEmpty()
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogMeetingJoinDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = MeetingJoinDetailsPresenterImpl(this)
        mMeeting = requireArguments().getParcelable(io.qurani.android.manager.App.MEETING)!!
        mBinding.meetingJoinDetailsUrlEdtx.addTextChangedListener(mTextWatcher)
        mBinding.meetingJoinDetailsCancelBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.meetingJoinDetailsCancelBtn -> {
                dismiss()
            }

            R.id.meetingJoinDetailsSaveBtn -> {
                val url = mBinding.meetingJoinDetailsUrlEdtx.text.toString()
                val password = mBinding.meetingJoinDetailsPasswordEdtx.text.toString()

                val reserveMeeting = ReserveMeeting()
                reserveMeeting.reservedMeetingId = mMeeting.id
                reserveMeeting.link = url
                if (password.isNotEmpty()) {
                    reserveMeeting.password = password
                }

                mPresenter.createJoin(reserveMeeting)
            }
        }
    }

    fun onMeetingJoinAdded(response: io.qurani.android.model.BaseResponse) {
        if (context == null) return

        if (response.isSuccessful) {
            dismiss()
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