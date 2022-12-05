package io.qurani.android.ui

import android.os.Bundle
import io.qurani.android.databinding.ActivitySignInBinding
import io.qurani.android.manager.App
import io.qurani.android.ui.frag.ChooseAuthFrag

class SignInActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = Bundle()
        intent?.getBooleanExtra(io.qurani.android.manager.App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, false)?.let {
            bundle.putBoolean(
                io.qurani.android.manager.App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP,
                it
            )
        }

        val frag = ChooseAuthFrag()
        frag.arguments = bundle

        supportFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
    }
}