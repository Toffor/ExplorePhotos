package com.example.coskun.explorephotos.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Coskun Yalcinkaya.
 */
data class Photos(
        @SerializedName("page")
        val page: Int,
        @SerializedName("pages")
        val totalPages: Int,
        @SerializedName("perpage")
        val photoCountInPerPage: Int,
        @SerializedName("total")
        val totalPhotoCount: Int,
        @SerializedName("photo")
        var photoList: MutableList<Photo>
)