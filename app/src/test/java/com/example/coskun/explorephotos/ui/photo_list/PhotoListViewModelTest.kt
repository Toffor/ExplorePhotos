package com.example.coskun.explorephotos.ui.photo_list

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.example.coskun.explorephotos.api.Response
import com.example.coskun.explorephotos.entity.Photo
import com.example.coskun.explorephotos.repository.PhotoRepository
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by Coskun Yalcinkaya.
 */
@Suppress("UNCHECKED_CAST")
class PhotoListViewModelTest {

    @Rule
    @JvmField val instantExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var repository : PhotoRepository
    private lateinit var viewModel : PhotoListViewModel
    private val keyword = "cat"

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        viewModel = PhotoListViewModel(repository)
    }

    @Test
    fun getPhotoList_shouldChangeWhenObserved(){
        val response : Response<List<Photo>> = Response.loading()
        whenever(repository.getPhotos(any(), any())).thenAnswer {
            (it.arguments[1] as (Response<List<Photo>>, Int) -> Unit).invoke(response, 1)
        }
        whenever(repository.searchPhotos(any(), any(), any())).thenAnswer{
            (it.arguments[2] as (Response<List<Photo>>, Int) -> Unit).invoke(response, 1)
        }
        val observer : Observer<Response<List<Photo>>> = mock()
        viewModel.getPhotoList().observeForever(observer)
        viewModel.getPhotos()
        viewModel.searchPhoto(keyword)
        verify(observer, times(2)).onChanged(response)
    }

    @Test
    fun getPhotoList_shouldNotChangeWhenNotObserved(){
        val response : Response<List<Photo>> = Response.loading()
        whenever(repository.getPhotos(any(), any())).thenAnswer {
            (it.arguments[1] as (Response<List<Photo>>, Int) -> Unit).invoke(response, 1)
        }
        whenever(repository.searchPhotos(any(), any(), any())).thenAnswer{
            (it.arguments[2] as (Response<List<Photo>>, Int) -> Unit).invoke(response, 1)
        }
        val observer : Observer<Response<List<Photo>>> = mock()
        viewModel.getPhotos()
        viewModel.searchPhoto(keyword)
        verify(observer, never()).onChanged(response)
    }

    @Test
    fun  getLoadMoreState_shouldChangeWhenFetchNextPageCalled(){
        val response : Response<List<Photo>> = Response.loading()
        whenever(repository.getPhotos(any(), any())).thenAnswer {
            (it.arguments[1] as (Response<List<Photo>>, Int) -> Unit).invoke(response, 1)
        }
        whenever(repository.searchPhotos(any(), any(), any())).thenAnswer{
            (it.arguments[2] as (Response<List<Photo>>, Int) -> Unit).invoke(response, 1)
        }
        val observer : Observer<PhotoListViewModel.LoadMoreState?> = mock()
        viewModel.getLoadMoreState().observeForever(observer)
        viewModel.getPhotos()
        viewModel.fetchNextPage()
        verify(observer).onChanged(any())
    }


    @Test
    fun getPhotos_onInitialize(){
        viewModel.getPhotos()
        verify(repository).getPhotos(eq(1), any())
    }

    @Test
    fun searchPhotos_onInitialize(){
        viewModel.searchPhoto(keyword)
        verify(repository).searchPhotos(eq(keyword), eq(1), any())
    }

    @Test
    fun fetchNextPage_shouldFetchNextPageWithoutKeyword(){
        whenever(repository.getPhotos(any(), any())).thenAnswer {
            val requestedPage = it.arguments[0] as Int
            (it.arguments[1] as ((Response<List<Photo>>, Int) -> Unit)).invoke(Response.success(listOf()), requestedPage + 1)
        }
        viewModel.getPhotos()
        viewModel.fetchNextPage()
        viewModel.fetchNextPage()
        verify(repository).getPhotos(eq(1), any())
        verify(repository).getPhotos(eq(2), any())
        verify(repository).getPhotos(eq(3), any())
    }

    @Test
    fun fetchNextPage_shouldFetchNextPageWithKeyword(){
        whenever(repository.searchPhotos(eq(keyword), any(), any())).thenAnswer {
            val requestedPage = it.arguments[1] as Int
            (it.arguments[2] as ((Response<List<Photo>>, Int) -> Unit)).invoke(Response.success(arrayListOf()), requestedPage + 1)
        }
        viewModel.searchPhoto(keyword)
        viewModel.fetchNextPage()
        viewModel.fetchNextPage()
        verify(repository).searchPhotos(eq(keyword), eq(1), any())
        verify(repository).searchPhotos(eq(keyword), eq(2), any())
        verify(repository).searchPhotos(eq(keyword), eq(3), any())
    }

    @Test
    fun retry_shouldCallGetPhotosOnError(){
        whenever(repository.getPhotos(eq(1), any())).thenAnswer {
            val requestedPage = it.arguments[0] as Int
            (it.arguments[1] as ((Response<List<Photo>>, Int) -> Unit)).invoke(Response.error(""), requestedPage)
        }
        viewModel.getPhotos()
        viewModel.retry()
        verify(repository, times(2)).getPhotos(eq(1), any())
    }

    @Test
    fun retry_shouldCallGetPhotosWithSamePageOnError(){
        whenever(repository.getPhotos(any(), any())).thenAnswer {
            var requestedPage = it.arguments[0] as Int
            var response : Response<List<Photo>> = Response.error("")
            if (requestedPage == 1){
                requestedPage ++
                response = Response.success(arrayListOf())
            }
            (it.arguments[1] as ((Response<List<Photo>>, Int) -> Unit)).invoke(response, requestedPage)
        }
        viewModel.getPhotos()
        viewModel.fetchNextPage()
        viewModel.retry()
        verify(repository).getPhotos(eq(1), any())
        verify(repository, times(2)).getPhotos(eq(2), any())
    }

    @Test
    fun retry_shouldCallSearchPhotosWithSameKeywordOnError(){
        whenever(repository.searchPhotos(eq(keyword), any(), any())).thenAnswer {
            val requestedPage = it.arguments[1] as Int
            (it.arguments[2] as (Response<List<Photo>>, Int) -> Unit).invoke(Response.error(""), requestedPage)
        }
        viewModel.searchPhoto(keyword)
        viewModel.retry()
        verify(repository, times(2)).searchPhotos(eq(keyword), eq(1), any())
    }

    @Test
    fun retry_shouldCallSearhPhotosWithSameKeywordAndPageOnError(){
        whenever(repository.searchPhotos(eq(keyword), any(), any())).thenAnswer {
            var requestedPage = it.arguments[1] as Int
            var response : Response<List<Photo>> = Response.error("")
            if (requestedPage == 1){
                requestedPage ++
                response = Response.success(arrayListOf())
            }
            (it.arguments[2] as ((Response<List<Photo>>, Int) -> Unit)).invoke(response, requestedPage)
        }

        viewModel.searchPhoto(keyword)
        viewModel.fetchNextPage()
        viewModel.retry()
        verify(repository).searchPhotos(eq(keyword), eq(1), any())
        verify(repository, times(2)).searchPhotos(eq(keyword), eq(2), any())
    }
}