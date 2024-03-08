package com.biho.visageverify.data.model

import android.graphics.Bitmap

interface PersonInterpreter {
    fun interpret(bitmap: Bitmap): Array<FloatArray>
}