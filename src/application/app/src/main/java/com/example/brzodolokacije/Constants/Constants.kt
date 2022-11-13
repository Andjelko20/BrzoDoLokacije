package com.example.brzodolokacije.Constants

import android.content.res.Resources

object Constants {
    const val BASE_URL = "http://10.0.2.2:7281/api/"

    var screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels
    var screenHeight= screenWidth*3/4
}