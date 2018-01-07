package com.example.coskun.explorephotos.api

import com.example.coskun.explorephotos.BuildConfig
import com.example.coskun.explorephotos.entity.PhotoResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Coskun Yalcinkaya.
 */
interface FlickrService {

    @GET("?api_key=${BuildConfig.API_KEY}&method=flickr.photos.getRecent&per_page=20&format=json&nojsoncallback=1")
    fun getRecentPhotos(@Query(value = "page") page: Int) : Observable<PhotoResponse>

    @GET("?api_key=${BuildConfig.API_KEY}&method=flickr.photos.search&per_page=20&format=json&nojsoncallback=1")
    fun searchPhoto(@Query(value = "text") keyword: String, @Query(value = "page") page: Int) : Observable<PhotoResponse>


}