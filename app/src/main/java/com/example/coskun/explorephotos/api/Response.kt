package com.example.coskun.explorephotos.api

/**
 * Created by Coskun Yalcinkaya.
 */

class Response<out T> private constructor(val status : Status, val data : T?, val errorMessage : String?){

    companion object {

        fun <T> success(response: T?) = Response(Status.SUCCESS, response, null)

        fun <T> error(errorMessage: String?) : Response<T> = Response(Status.ERROR, null, errorMessage)

        fun <T> loading(response: T?) : Response<T> = Response(Status.LOADING, response, null)

        fun <T> loading() : Response<T> = Response(Status.LOADING, null, null)

    }
}