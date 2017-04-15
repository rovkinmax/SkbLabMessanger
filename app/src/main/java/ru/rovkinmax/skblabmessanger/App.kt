package ru.rovkinmax.skblabmessanger

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.backendless.Backendless
import timber.log.Timber

class App : Application() {
    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Backendless.setUrl(BuildConfig.API_BACKENDLESS)
        Backendless.initApp(this, BuildConfig.APP_ID_BACKENDLESS, BuildConfig.API_KEY_BACKENDLESS)
        Timber.plant(Timber.DebugTree())
    }
}