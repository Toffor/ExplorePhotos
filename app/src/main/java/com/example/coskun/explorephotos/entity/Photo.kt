package com.example.coskun.explorephotos.entity

/**
 * Created by Coskun Yalcinkaya.
 */
data class Photo(
        val id: String,
        val title: String,
        val owner: String,
        val farm: String,
        val secret: String,
        val server: String
)