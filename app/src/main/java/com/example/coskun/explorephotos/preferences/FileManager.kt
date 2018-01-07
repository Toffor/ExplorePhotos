package com.example.coskun.explorephotos.preferences

import android.app.Application
import android.content.Context
import com.example.coskun.explorephotos.entensions.getObject
import com.example.coskun.explorephotos.entensions.serializeAndPutObject
import javax.inject.Inject

/**
 * Created by Coskun Yalcinkaya.
 */
class FileManager @Inject constructor(val app: Application) {

    companion object {
        private const val PREF_NAME = "photos_pref"
        private const val KEY_SUGGESTIONS = "suggestions"
    }

    private val sharedPreferences = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSuggestions(suggestionList: ArrayList<String>){
        sharedPreferences.serializeAndPutObject(KEY_SUGGESTIONS, suggestionList)
    }

    fun getSuggestions() : ArrayList<String>{
        var suggestionList = sharedPreferences.getObject<ArrayList<String>>(KEY_SUGGESTIONS)
        if (suggestionList == null) suggestionList = arrayListOf()
        return suggestionList
    }
}