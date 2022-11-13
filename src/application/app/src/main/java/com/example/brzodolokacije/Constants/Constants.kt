package com.example.brzodolokacije.Constants

import android.content.res.Resources

object Constants {
    const val BASE_URL = "http://softeng.pmf.kg.ac.rs:10051/api/"

    var screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels
    var screenHeight= screenWidth*3/4
}