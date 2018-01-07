package com.example.coskun.explorephotos.ui.common

import android.graphics.Bitmap
import android.support.v4.view.ViewCompat
import android.widget.ImageView
import com.example.coskun.explorephotos.MainActivity
import com.example.coskun.explorephotos.R
import com.example.coskun.explorephotos.ui.PhotoDetails.PhotoDetailFragment
import com.example.coskun.explorephotos.ui.photo_list.PhotoListFragment
import javax.inject.Inject

/**
 * Created by Coskun Yalcinkaya.
 */
class Navigator @Inject constructor(private val activity: MainActivity) {

    private val fragmentManger = activity.supportFragmentManager

    fun navigateToPhotoListFragment(){
        fragmentManger.beginTransaction()
                .replace(R.id.fragmentContainer, PhotoListFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    fun navigateToPhotoDetailsFragment(transactionName: String, view: ImageView, bitmap: Bitmap){
        fragmentManger.beginTransaction()
                .replace(R.id.fragmentContainer, PhotoDetailFragment.newInstance(transactionName, bitmap))
                .addSharedElement(view, ViewCompat.getTransitionName(view))

        .addToBackStack(null)
                .commit()
    }

    fun popBackStack(){
        if (fragmentManger.backStackEntryCount > 1) {
            fragmentManger.popBackStack()
        }else{
            activity.finish()
        }
    }
}