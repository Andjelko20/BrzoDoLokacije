package com.example.brzodolokacije.Posts

import android.graphics.Bitmap

object SelectedPhoto {

    private var selectedBitmap : Bitmap? = null

    fun saveBitmap(bm : Bitmap?)
    {
        selectedBitmap = bm
    }

    fun returnSavedBitmap() : Bitmap?
    {
        return selectedBitmap
    }
}