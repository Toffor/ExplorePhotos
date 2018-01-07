package com.example.coskun.explorephotos.di

import com.example.coskun.explorephotos.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Coskun Yalcinkaya.
 */

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [(FragmentBuilders::class)])
    abstract fun contributeMainActivity() : MainActivity
}