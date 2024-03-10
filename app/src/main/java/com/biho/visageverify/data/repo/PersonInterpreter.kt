package com.biho.visageverify.data.repo

import android.graphics.Bitmap

interface PersonInterpreter {
    fun interpret(bitmap: Bitmap): Array<FloatArray>
}