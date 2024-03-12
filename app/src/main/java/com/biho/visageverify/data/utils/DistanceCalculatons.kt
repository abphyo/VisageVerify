package com.biho.visageverify.data.utils

import kotlin.math.pow
import kotlin.math.sqrt

fun l2Norm(x1: FloatArray, x2: FloatArray): Float {
    var sum = 0.0f
    val mag1 = sqrt(x1.map { xi -> xi.pow(2) }.sum())
    val mag2 = sqrt(x2.map { xi -> xi.pow(2) }.sum())
    for (i in x1.indices) {
        sum += ((x1[i] / mag1) - (x2[i] / mag2)).pow(2)
    }
    return sqrt(sum)
}

fun cosineSim(x1: FloatArray, x2: FloatArray): Float {
    var dotProduct = 0.0f
    var normA = 0.0f
    var normB = 0.0f
    for (i in x1.indices) {
        dotProduct += x1[i] * x2[i]
        normA += x1[i].pow(2)
        normB += x2[i].pow(2)
    }
    return dotProduct / (sqrt(normA) * sqrt(normB))
}