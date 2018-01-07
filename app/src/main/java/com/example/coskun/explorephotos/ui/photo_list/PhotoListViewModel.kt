package com.example.coskun.explorephotos.ui.photo_list

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.coskun.explorephotos.api.Response
import com.example.coskun.explorephotos.api.Status
import com.example.coskun.explorephotos.entity.Photo
import com.example.coskun.explorephotos.repository.PhotoRepository
import javax.inject.Inject

/**
 * Created by Coskun Yalcinkaya.
 */
class PhotoListViewModel @Inject constructor(private val photoRepository: PhotoRepository) : ViewModel() {

    private enum class LastAction{
        GET_PHOTOS, SEARCH_PHOTO, FETCH_NEXT_PAGE
    }

    private var lastAction = LastAction.GET_PHOTOS

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
        lastAction = LastAction.GET_PHOTOS
        photoRepository.getPhotos(1, { response, nextPage ->
            photos.value = response
            nextPageHandler.nextPage = nextPage
            nextPageHandler.keyword = null
        })

    }

    fun getLoadMoreState() : LiveData<LoadMoreState?> = nextPageHandler.getLoadMoreState()

    fun searchPhoto(keyword: String){
        searchPhoto(keyword, false)
    }

    private fun searchPhoto(keyword: String, retry: Boolean){
        if (retry || (!keyword.isEmpty() && lastSearchedKeyword != keyword)){
            photoRepository.clearDisposables()
            lastAction = LastAction.SEARCH_PHOTO
            photoRepository.searchPhotos(keyword, 1, {response, nextPage ->
                photos.value = response
                lastSearchedKeyword = keyword
                nextPageHandler.nextPage = nextPage
                nextPageHandler.keyword = keyword
            })
        }
    }

    fun fetchNextPage() = nextPageHandler.fetchNextPage()


    fun retry(){
        when (lastAction){
            LastAction.GET_PHOTOS -> getPhotos()
            LastAction.SEARCH_PHOTO -> searchPhoto(lastSearchedKeyword!!, true)
            LastAction.FETCH_NEXT_PAGE -> fetchNextPage()
        }
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