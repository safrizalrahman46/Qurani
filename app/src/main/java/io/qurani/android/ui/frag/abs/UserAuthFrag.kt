package io.qurani.android.ui.frag.abs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.CallbackManagerImpl
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import io.qurani.android.R
import io.qurani.android.manager.App
import io.qurani.android.manager.ResultContracts
import io.qurani.android.manager.ToastMaker
import io.qurani.android.manager.db.AppDb
import io.qurani.android.manager.listener.ItemCallback
import io.qurani.android.manager.net.ApiService
import io.qurani.android.manager.net.observer.NetworkObserverFragment
import io.qurani.android.model.*
import io.qurani.android.presenterImpl.CommonApiPresenterImpl
import io.qurani.android.ui.MainActivity
import io.qurani.android.ui.widget.LoadingDialog

abstract class UserAuthFrag : io.qurani.android.manager.net.observer.NetworkObserverFragment(), View.OnClickListener {

    protected lateinit var mGoogleSignInClient: GoogleSignInClient
    protected lateinit var mGoogleBtn: View
    protected lateinit var mFacebookBtn: View
    private lateinit var mFacebookCallbackManager: CallbackManager
    private lateinit var mGoogleLoginContract: ActivityResultLauncher<GoogleSignInClient>
    protected var mLoadingDialog: LoadingDialog? = null
    protected var mSignInRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGoogleLoginContract = registerForActivityResult(ResultContracts.GoogleLogin()) { task ->
            handleGoogleSignInResult(task)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        if (arguments != null) {
            mSignInRequest =
                requireArguments().getBoolean(io.qurani.android.manager.App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, false)
        }

        initGoogleSignIn()
        initFacebookSignIn()
        mGoogleBtn.setOnClickListener(this)
        mFacebookBtn.setOnClickListener(this)
    }

    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_server_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun initFacebookSignIn() {
        mFacebookCallbackManager = CallbackManager.Factory.create()
    }

    override fun onClick(v: View?) {
        if (v?.id == mGoogleBtn.id) {
            mGoogleLoginContract.launch(mGoogleSignInClient)
        } else if (v?.id == mFacebookBtn.id) {
            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog?.show(childFragmentManager, null)

            val loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(this, arrayListOf("email", "public_profile"))

            loginManager.registerCallback(mFacebookCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        if (result != null) {
                            getFacebookSignInCallback().onSuccess(result)
                        } else {
                            mLoadingDialog?.dismiss()
                        }
                    }

                    override fun onCancel() {
                        mLoadingDialog?.dismiss()
                        getFacebookSignInCallback().onCancel()
                    }

                    override fun onError(error: FacebookException?) {
                        mLoadingDialog?.dismiss()
                        getFacebookSignInCallback().onError(error)
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    fun onErrorOccured(error: io.qurani.android.model.BaseResponse?) {
        mLoadingDialog?.dismiss()

        if (error != null) {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                error.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onThirdPartyLogin(
        res: Data<io.qurani.android.model.Response>,
        provider: Int,
        thirdPartyLogin: ThirdPartyLogin
    ) {
        if (res.isSuccessful) {
            if (provider == io.qurani.android.manager.App.Companion.RegistrationProvider.GOOGLE.value()) {
                mGoogleSignInClient.signOut()
            } else if (provider == io.qurani.android.manager.App.Companion.RegistrationProvider.FACEBOOK.value()) {
                LoginManager.getInstance().logOut()
            }

            thirdPartyLogin.userId = res.data!!.userId

            if (res.data!!.isAlreadyRegistered) {
                val token = res.data!!.token
                io.qurani.android.manager.App.saveToLocal(token, requireContext(), AppDb.DataType.TOKEN)
                ApiService.createAuthorizedApiService(token)

                val commonPresenter = CommonApiPresenterImpl.getInstance()
                commonPresenter.getUserInfo(res.data!!.userId, object : ItemCallback<UserProfile> {
                    override fun onItem(item: UserProfile, vararg args: Any) {
                        io.qurani.android.manager.App.saveToLocal(
                            Gson().toJson(item),
                            requireContext(),
                            AppDb.DataType.USER
                        )
                        io.qurani.android.manager.App.loggedInUser = item
                        goToNextPageFromThirdparty(res, thirdPartyLogin)
                    }
                })
            } else {
                goToNextPageFromThirdparty(res, thirdPartyLogin)
            }
        } else {
            onErrorOccured(res)
        }
    }


    private fun goToNextPageFromThirdparty(res: Data<io.qurani.android.model.Response>, thirdPartyLogin: ThirdPartyLogin) {
        activity?.finish()

        if (mSignInRequest) {
            val intent = Intent()
            intent.putExtra(
                io.qurani.android.manager.App.SHOULD_REGISTER,
                !res.data!!.isAlreadyRegistered
            )
            intent.putExtra(io.qurani.android.manager.App.USER, thirdPartyLogin)
            activity?.setResult(Activity.RESULT_OK, intent)
        } else {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(io.qurani.android.manager.App.SHOULD_REGISTER, !res.data!!.isAlreadyRegistered)
            intent.putExtra(io.qurani.android.manager.App.USER, thirdPartyLogin)
            startActivity(intent)
        }
    }

    protected abstract fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>)

    protected abstract fun getFacebookSignInCallback(): FacebookCallback<LoginResult>
}