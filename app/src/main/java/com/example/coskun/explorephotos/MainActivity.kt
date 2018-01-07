package com.example.coskun.explorephotos

import android.graphics.Color
import android.os.Bundle
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.example.coskun.explorephotos.ui.common.Navigator
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        if (savedInstanceState == null) navigator.navigateToPhotoListFragment()
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onSupportNavigateUp(): Boolean {
        navigator.popBackStack()
        return true
    }

    override fun onBackPressed() {
        navigator.popBackStack()
    }

    fun animateToolbar(){
        TransitionManager.beginDelayedTransition(toolbar)
    }

}
