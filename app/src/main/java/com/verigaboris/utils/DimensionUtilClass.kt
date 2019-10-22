package com.verigaboris.utils

import android.content.Context
import android.util.TypedValue


object DimensionsUtil {

    fun getPixelsFromDp(context: Context, dp: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics).toInt()
    }

    fun getPixelsFromSp(context: Context, sp: Float): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, r.displayMetrics)
    }

}
