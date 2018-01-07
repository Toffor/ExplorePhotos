package com.example.coskun.explorephotos.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.coskun.explorephotos.ui.photo_list.PhotoListViewModel
import com.example.coskun.explorephotos.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by Coskun Yalcinkaya.
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PhotoListViewModel::class)
    abstract fun bindUserViewModel(photoListViewModel: PhotoListViewModel) : ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory) : ViewModelProvider.Factory
}