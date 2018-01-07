package com.example.coskun.explorephotos.entensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Coskun Yalcinkaya.
 */

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)!!
