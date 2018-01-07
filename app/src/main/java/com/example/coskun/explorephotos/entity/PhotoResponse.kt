package com.example.coskun.explorephotos.entity

import com.google.gson.annotations.SerializedName

/**
 * Created by Coskun Yalcinkaya.
 */
data class PhotoResponse(
        @SerializedName("photos")
        var photos: Photos?,
        @SerializedName("stat")
        val status: String,
        @SerializedName("code")
        val code: Int?,
        @SerializedName("message")
        val errorMessage: String?
)