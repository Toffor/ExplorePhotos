package com.example.coskun.explorephotos

import android.app.Activity
import android.app.Application
import android.content.res.Resources
import com.example.coskun.explorephotos.di.AppInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Coskun Yalcinkaya.
 */

class App : Application(), HasActivityInjector{

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        resource = resources
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun activityInjector() = dispatchingAndroidInjector

    companion object {
        lateinit var resource: Resources
    }
}