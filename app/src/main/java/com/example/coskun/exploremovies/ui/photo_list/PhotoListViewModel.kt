package com.example.coskun.exploremovies.ui.photo_list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.coskun.exploremovies.api.Response
import com.example.coskun.exploremovies.api.Status
import com.example.coskun.exploremovies.entity.Photo
import com.example.coskun.exploremovies.repository.PhotoRepository
import javax.inject.Inject

/**
 * Created by Coskun Yalcinkaya.
 */
class PhotoListViewModel @Inject constructor(private val photoRepository: PhotoRepository) : ViewModel() {

    private val photos = MutableLiveData<Response<List<Photo>>>()

    private var lastSearchedKeyword : String? = null

    private val nextPageHandler = NextPageHandler(photoRepository) {
        val mergedList = photos.value!!.data!! + it.data!!
        photos.value = Response.success(mergedList)
    }

    fun getPhotos() = getPhotos(true)

    fun getPhotos(initialize: Boolean) {
        if (initialize &&  photos.value?.data != null) return
        photoRepository.clearDisposables()
        photoRepository.getPhotos(1, { response, nextPage ->
            photos.value = response
            nextPageHandler.nextPage = nextPage
            nextPageHandler.keyword = null
        })

    }

    fun getLoadMoreState() : LiveData<LoadMoreState?> = nextPageHandler.getLoadMoreState()


    fun searchPhoto(keyword: String){
        if (keyword.isEmpty() || lastSearchedKeyword == keyword) return
        photoRepository.clearDisposables()
        photoRepository.searchPhotos(keyword, 1, {response, nextPage ->
            photos.value = response
            nextPageHandler.nextPage = nextPage
            nextPageHandler.keyword = keyword
        })
    }

    fun fetchNextPage() = nextPageHandler.fetchNextPage()


    fun retry(){

    }

    class LoadMoreState(val running: Boolean = false, private val errorMessage: String? = null){

        private var handleError = false

        fun getErrorMessageIfNotHandled() : String? {
            if (handleError) return null
            handleError = true
            return errorMessage
        }
    }

    class NextPageHandler(private val photoRepository: PhotoRepository, private val photoList: (Response<List<Photo>>) -> Unit) {

        var nextPage = 0
        var keyword : String? = null

        private val loadMoreState = MutableLiveData<LoadMoreState?>()

        fun getLoadMoreState() = loadMoreState

        fun fetchNextPage() {

            if (loadMoreState.value?.running == true || nextPage == 0) return

            if (keyword.isNullOrEmpty()) fetchRecentPhotosNextPage() else searchNextPage()
        }

        private fun fetchRecentPhotosNextPage(){
            loadMoreState.value = LoadMoreState(true)
            photoRepository.getPhotos(nextPage, { response, i ->

                when (response.status) {

                    Status.SUCCESS -> {
                        loadMoreState.value = LoadMoreState()
                        photoList.invoke(response)
                    }

                    Status.ERROR -> {
                        loadMoreState.value = LoadMoreState(false, response.errorMessage)
                    }

                    else -> {
                    }
                }
                nextPage = i

            })
        }

        private fun searchNextPage(){
            loadMoreState.value = LoadMoreState(true)

            photoRepository.searchPhotos(keyword!!, nextPage, { response, i ->

                when (response.status) {

                    Status.SUCCESS -> {
                        loadMoreState.value = LoadMoreState()
                        photoList.invoke(response)
                    }

                    Status.ERROR -> {
                        loadMoreState.value = LoadMoreState(false, response.errorMessage)
                    }

                    else -> {
                    }
                }
                nextPage = i
            })
        }
    }


    fun getPhotoList() : LiveData<Response<List<Photo>>> = photos

    override fun onCleared() = photoRepository.clearDisposables()

}