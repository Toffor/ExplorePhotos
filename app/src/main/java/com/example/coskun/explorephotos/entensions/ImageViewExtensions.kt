package com.example.coskun.explorephotos.entensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.coskun.explorephotos.R

/**
 * Created by Coskun Yalcinkaya.
 */

fun ImageView.setImageUrl(url: String){
    Glide.with(this).load(url)
            .apply(RequestOptions().override(200, 200)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.place_holder))
            .into(this)
}