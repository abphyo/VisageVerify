package com.biho.visageverify.domain

import com.biho.visageverify.data.model.Classification
import kotlin.math.pow
import kotlin.math.sqrt

class ValidateDataUseCase(
    private val faceDetectionUseCase: FaceDetectionUseCase
) {

    private lateinit var classifications: List<Classification>

    operator fun invoke(euclidean: Array<FloatArray>) {

    }

    private fun cosineSim(x1: FloatArray, x2: FloatArray): Float {
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

}