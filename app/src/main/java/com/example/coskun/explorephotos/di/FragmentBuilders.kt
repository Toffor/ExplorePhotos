package com.example.coskun.explorephotos.di

import com.example.coskun.explorephotos.ui.photo_list.PhotoListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by Coskun Yalcinkaya.
 */

@Module
abstract class FragmentBuilders {

    @ContributesAndroidInjector
    abstract fun contributePhotoListFragment() : PhotoListFragment
}