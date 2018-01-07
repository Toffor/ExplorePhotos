package com.example.coskun.explorephotos.di

import android.app.Application
import com.example.coskun.explorephotos.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Created by Coskun Yalcinkaya.
 */
@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AppModule::class), (MainActivityModule::class)])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun inject(application: App)
}

