package com.example.coskun.explorephotos.ui.photo_list

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Coskun Yalcinkaya.
 */
class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {

        outRect!!.left = space
        outRect.right = space
        outRect.bottom = space * 2

        if (parent!!.getChildLayoutPosition(view) == 0){
            outRect.top = space
        }else {
            outRect.top = 0
        }
    }
}