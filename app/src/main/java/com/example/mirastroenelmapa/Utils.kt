package com.example.mirastroenelmapa

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.ViewBinding

object Utils {
    var binding: ViewBinding? = null

    fun dp(pixeles: Int): Int {
        if (binding == null) return 0
        val escala = binding!!.root.resources.displayMetrics.density
        return (escala * pixeles + 0.5f).toInt()
    }

    fun getBitmapfromVector(context: Context, resId: Int): Bitmap?{
        return AppCompatResources.getDrawable(context, resId)?.toBitmap()
    }
}