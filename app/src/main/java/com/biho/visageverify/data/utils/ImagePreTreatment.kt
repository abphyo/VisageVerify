package com.biho.visageverify.data.utils

import android.graphics.Bitmap
import kotlin.math.ceil

fun Bitmap.bitmapToNV21ByteArray(): ByteArray {
    val argb = IntArray(this.width * this.height)
    this.getPixels(argb, 0, this.width, 0, 0, this.width, this.height)
    val yuv = ByteArray(
        this.height * this.width + 2 * ceil(this.height / 2.0).toInt()
                * ceil(this.width / 2.0).toInt()
    )
    encodeYUV420SP(yuv, argb, this.width, this.height)
    return yuv
}

private fun encodeYUV420SP(yuv420sp: ByteArray, argb: IntArray, width: Int, height: Int) {
    val frameSize = width * height
    var yIndex = 0
    var uvIndex = frameSize
    var r: Int
    var g: Int
    var b: Int
    var y: Int
    var u: Int
    var v: Int
    var index = 0
    for (j in 0 until height) {
        for (i in 0 until width) {
            r = argb[index] and 0xff0000 shr 16
            g = argb[index] and 0xff00 shr 8
            b = argb[index] and 0xff shr 0
            y = (66 * r + 129 * g + 25 * b + 128 shr 8) + 16
            u = (-38 * r - 74 * g + 112 * b + 128 shr 8) + 128
            v = (112 * r - 94 * g - 18 * b + 128 shr 8) + 128
            yuv420sp[yIndex++] = (if (y < 0) 0 else if (y > 255) 255 else y).toByte()
            if (j % 2 == 0 && index % 2 == 0) {
                yuv420sp[uvIndex++] = (if (v < 0) 0 else if (v > 255) 255 else v).toByte()
                yuv420sp[uvIndex++] = (if (u < 0) 0 else if (u > 255) 255 else u).toByte()
            }
            index++
        }
    }
}