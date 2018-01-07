package com.example.coskun.explorephotos.ui.photo_list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.transition.Fade
import android.support.v4.widget.CursorAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.example.coskun.explorephotos.BaseFragment
import com.example.coskun.explorephotos.MainActivity
import com.example.coskun.explorephotos.R
import com.example.coskun.explorephotos.api.Status
import com.example.coskun.explorephotos.di.Injectable
import com.example.coskun.explorephotos.preferences.FileManager
import com.example.coskun.explorephotos.ui.common.Navigator
import kotlinx.android.synthetic.main.fragment_photo_list.*
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Coskun Yalcinkaya.
 */

class PhotoListFragment() : BaseFragment(), Injectable{

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var navigator: Navigator
    @Inject lateinit var fileManager: FileManager
    private lateinit var photoListViewModel: PhotoListViewModel
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var suggestionList: ArrayList<String>
    private lateinit var snackbar: Snackbar

    private val photoAdapter = PhotoAdapter{
        handleClick(it)
    }

    override fun getLayoutId() = R.layout.fragment_photo_list

    override fun getToolbarTitle() = R.string.toolbar_title_photos

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
        suggestionAdapter = SuggestionAdapter(context!!, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        val layoutManager = object : GridLayoutManager(context, 2){
            override fun supportsPredictiveItemAnimations() = false
        }
        rv_photo.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount
                if (lastVisibleItemPosition > totalItemCount - 5 && totalItemCount != 0 && !snackbar.isShown){
                    photoListViewModel.fetchNextPage()
                }
            }
        })
        rv_photo.layoutManager = layoutManager
        rv_photo.addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
        rv_photo.adapter = photoAdapter
    }

    override fun onResume() {
        super.onResume()
        suggestionList = fileManager.getSuggestions()
    }

    override fun onPause() {
        super.onPause()
        fileManager.saveSuggestions(suggestionList)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        photoListViewModel = ViewModelProviders.of(this, viewModelFactory).get(PhotoListViewModel::class.java)
        observePhoto()
        observeLoadMoreStatus()
        photoListViewModel.getPhotos()
    }

    private fun observePhoto(){
        photoListViewModel.getPhotoList().observe(this, Observer {
            Timber.d("getPhotoList ${it!!.status}")
            when (it.status){
                Status.LOADING -> {
                    prg_loading.visibility = View.VISIBLE
                    prg_loadMoreState.visibility = View.GONE
                    img_noDataFound.visibility = View.GONE
                    photoAdapter.updateData(it.data!!)
                }

                Status.SUCCESS -> {
                    prg_loading.visibility = View.GONE
                    photoAdapter.updateData(it.data!!)
                    if (it.data.isEmpty()) img_noDataFound.visibility = View.VISIBLE

                }

                Status.ERROR ->{
                    prg_loading.visibility = View.GONE
                    img_noDataFound.visibility = View.GONE
                    showError(it.errorMessage!!)
                }
            }
        })
    }

    private fun observeLoadMoreStatus(){
        photoListViewModel.getLoadMoreState().observe(this, Observer {
            Timber.d("getLoadMoreState ${it!!.running}")
            if (it.running) prg_loadMoreState.visibility = View.VISIBLE
            else {
                prg_loadMoreState.visibility = View.GONE
                val errorMessage = it.getErrorMessageIfNotHandled()
                if (!errorMessage.isNullOrEmpty())
                    showError(errorMessage!!)
            }
        })
    }

    private fun showError(errorMessage: String){
        snackbar.setText(errorMessage).
        setAction(getString(R.string.retry), {
            photoListViewModel.retry()
        }).show()

    }

    private fun handleClick(imageView: ImageView){
        val transitionName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) imageView.transitionName else ""
        navigator.navigateToPhotoDetailsFragment(transitionName, imageView, (imageView.drawable as BitmapDrawable).bitmap)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.search_menu, menu)
        var filteredSuggestionList = listOf<String>()
        val searchMenuItem = menu!!.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        searchView.suggestionsAdapter = suggestionAdapter
        val editText = searchView.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
        editText.setHint(R.string.toolbar_search_hint)
        editText.setTextColor(Color.WHITE)
        editText.setHintTextColor(Color.WHITE)
        searchView.setOnSearchClickListener({
            val fadeTransition = Fade()
            fadeTransition.duration = 1500
            ((activity as MainActivity)).animateToolbar()
            searchMenuItem.expandActionView()
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    photoListViewModel.searchPhoto(query)
                    if(!suggestionList.contains(query)) suggestionList.add(query)
                }
                searchView.setQuery("", false)
                searchView.isIconified = true
                searchMenuItem.collapseActionView()
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filteredSuggestionList = suggestionAdapter.populateSuggestions(suggestionList, newText)
                }
                return true
            }

        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener{
            override fun onSuggestionSelect(position: Int) =  false

            override fun onSuggestionClick(position: Int): Boolean {
                Timber.d(filteredSuggestionList.toString())
                Timber.d(filteredSuggestionList[position])
                Timber.d(position.toString())
                searchView.setQuery(filteredSuggestionList[position], true)
                return true
            }

        })


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId){
            R.id.action_recent_photos -> photoListViewModel.getPhotos(false)
        }
        return true
    }

    companion object {
        fun newInstance() = PhotoListFragment()
    }

}
