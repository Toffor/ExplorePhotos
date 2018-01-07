package com.example.coskun.explorephotos.entensions

import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Created by Coskun Yalcinkaya.
 */
inline fun <reified T> SharedPreferences.getObject(key: String) : T? {
    val stored = this.getString(key, "")
    if (stored.isNullOrEmpty()) return null
    return Gson().fromJson(stored)
}

fun SharedPreferences.serializeAndPutObject(key: String, obj: Any){
    this.edit().putString(key,Gson().toJson(obj)).apply()
}