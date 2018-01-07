package com.example.coskun.explorephotos.ui.photo_list

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.support.v4.widget.CursorAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.coskun.explorephotos.R
import com.example.coskun.explorephotos.entensions.inflate

/**
 * Created by Coskun Yalcinkaya.
 */
class SuggestionAdapter(context: Context, cursor: Cursor?, flag: Int) : CursorAdapter(context, cursor, flag) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?) = parent!!.inflate(R.layout.item_suggestion)

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val textView = view!!.findViewById<TextView>(R.id.txt_suggestion)
        textView.text = cursor?.getString(cursor.getColumnIndexOrThrow("suggestion"))
    }

    fun populateSuggestions(suggestions: ArrayList<String>, query: String){
        val matrixCursor = MatrixCursor(arrayOf(BaseColumns._ID, "suggestion"))
        var count = 0
        val regex = Regex(createRegex(query))
        suggestions.forEach {
            if (it.matches(regex)){
                matrixCursor.addRow(arrayOf(count++, it))
            }
        }
        this.changeCursor(matrixCursor)
    }

    private fun createRegex(query: String) : String{
        val charArray = query.toCharArray()
        val sb = StringBuilder()
        for (c in charArray){
            sb.append('.', '*', c, '.', '*')
        }
        return sb.toString()
    }
}