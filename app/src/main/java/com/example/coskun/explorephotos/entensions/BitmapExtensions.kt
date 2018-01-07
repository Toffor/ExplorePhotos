package com.example.coskun.explorephotos.entensions

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Created by Coskun Yalcinkaya.
 */
fun Bitmap.toByteArray() : ByteArray{
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}