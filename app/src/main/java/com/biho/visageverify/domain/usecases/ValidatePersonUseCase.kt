package com.biho.visageverify.domain.usecases

import android.graphics.Bitmap
import kotlin.math.pow
import kotlin.math.sqrt

class ValidatePersonUseCase(
    private val getPersonsUseCase: GetPersonsUseCase,
    private val calculateLikenessUseCase: CalculateLikenessUseCase
) {
    private fun Array<FloatArray>.flatten(): FloatArray {
        val flattenedSize = sumOf { it.size }
        val result = FloatArray(flattenedSize)
        var index = 0
        for (array in this) {
            for (value in array) {
                result[index++] = value
            }
        }
        return result
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

    suspend operator fun invoke(croppedBitmap: Bitmap): String? {
        val likeness =
            calculateLikenessUseCase.interpretBitmap(bitmap = croppedBitmap) ?: emptyArray()
        return getPersonsUseCase.invoke().value.maxByOrNull { person ->
            cosineSim(x1 = likeness.flatten(), x2 = person.likeness.flatten())
        }?.name
    }

}
