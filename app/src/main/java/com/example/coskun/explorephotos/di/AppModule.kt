package com.example.coskun.explorephotos.di

import com.example.coskun.explorephotos.api.FlickrService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Coskun Yalcinkaya.
 */
@Module(includes = [(ViewModelModule::class)])
class AppModule {


    @Singleton
    @Provides
    fun provideService() = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/rest/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(FlickrService::class.java)!!

}