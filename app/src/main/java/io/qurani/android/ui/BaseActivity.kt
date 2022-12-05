package io.qurani.android.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.qurani.android.manager.App
import io.qurani.android.model.Language
import io.qurani.android.ui.frag.ErrorFrag
import java.util.*

open class BaseActivity : AppCompatActivity() {

    companion object {
        var language: Language? = null
    }

    override fun attachBaseContext(newBase: Context?) {
        try {
            super.attachBaseContext(newBase?.setAppLocale(language!!.code))
        } catch (ex: NullPointerException) {
            try {
                super.attachBaseContext(newBase)
            } catch (ex: NullPointerException) {
            }
        }
    }

    private fun Context.setAppLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return createConfigurationContext(config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        io.qurani.android.manager.App.currentActivity = this
    }

    override fun onResume() {
        super.onResume()
        io.qurani.android.manager.App.currentActivity = this
    }

    override fun onBackPressed() {
        if (ErrorFrag.isFragVisible) {
            io.qurani.android.manager.App.showExitDialog(this)
        } else {
            super.onBackPressed()
        }
    }
}