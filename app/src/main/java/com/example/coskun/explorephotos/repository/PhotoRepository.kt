package com.example.coskun.explorephotos.repository

import com.example.coskun.explorephotos.App
import com.example.coskun.explorephotos.R
import com.example.coskun.explorephotos.api.FlickrService
import com.example.coskun.explorephotos.api.Response
import com.example.coskun.explorephotos.entity.Photo
import com.example.coskun.explorephotos.entity.PhotoResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

/**
 * Created by Coskun Yalcinkaya.
 */
class PhotoRepository @Inject constructor(private val flickrService: FlickrService) {

    private val compositeDisposable = CompositeDisposable()

    fun getPhotos(page : Int, callback: (Response<List<Photo>>, Int) -> Unit){
        Timber.d("$page")
        compositeDisposable.add(flickrService.getRecentPhotos(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { callback.invoke(Response.loading(emptyList()), page) }
                .subscribeWith(object : DisposableObserver<PhotoResponse>() {
                    override fun onError(e: Throwable?) {
                        handleException(e, page, callback)
                    }

                    override fun onNext(photoResponse: PhotoResponse) {
                        when (photoResponse.status){
                            "ok" -> {
                                val currentPage = photoResponse.photos!!.page
                                val totalPage = photoResponse.photos!!.totalPages
                                val nextPage = if (currentPage < totalPage) currentPage + 1 else 0
                                callback.invoke(Response.success(photoResponse.photos?.photoList ?: mutableListOf()), nextPage)
                            }
                            else -> callback.invoke(Response.error(photoResponse.errorMessage), page)
                        }
                    }

                    override fun onComplete() {

                    }
                }))
    }

    fun searchPhotos(keyword: String, page: Int, callback: (Response<List<Photo>>, Int) -> Unit){
        Timber.d("$keyword $page")
        compositeDisposable.add(flickrService.searchPhoto(keyword, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { callback.invoke(Response.loading(listOf()), page)}
                .subscribeWith(object : DisposableObserver<PhotoResponse>(){

                    override fun onNext(photoResponse: PhotoResponse) {
                        when (photoResponse.status){
                            "ok" -> {
                                val currentPage = photoResponse.photos!!.page
                                val totalPage = photoResponse.photos!!.totalPages
                                val nextPage = if (currentPage < totalPage) currentPage + 1 else 0
                                callback.invoke(Response.success(photoResponse.photos?.photoList ?: mutableListOf()), nextPage)
                            }
                            else -> callback.invoke(Response.error(photoResponse.errorMessage), page)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        handleException(e, page, callback)
                    }

                    override fun onComplete() {
                    }

                }))
    }

    private fun handleException(throwable: Throwable?, page: Int, callback: (Response<List<Photo>>, Int) -> Unit){
        var errorMessage = throwable?.message ?: App.resource.getString(R.string.an_error_occurred_in_server)
        if (throwable is UnknownHostException) errorMessage = App.resource.getString(R.string.no_internet_connection)
        callback.invoke(Response.error(errorMessage), page)
    }

    fun clearDisposables() = compositeDisposable.clear()

}