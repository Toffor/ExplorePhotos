package com.example.coskun.explorephotos

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Coskun Yalcinkaya.
 *
 * Base Fragment class for common operations.
 */
abstract class BaseFragment : Fragment() {

    lateinit var toolbar: ActionBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(supportBackButton())
        toolbar.setDisplayShowHomeEnabled(supportBackButton())
        if (getToolbarTitle() !=  0) toolbar.title = getString(getToolbarTitle())
    }

    abstract fun getLayoutId() : Int

    protected open fun supportBackButton() = false

    @StringRes protected open fun getToolbarTitle() = 1

}