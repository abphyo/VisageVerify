package com.biho.visageverify.domain.usecases

import android.graphics.Bitmap
import android.util.Log
import com.biho.visageverify.data.utils.cosineSim
import com.biho.visageverify.data.utils.l2Norm
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

    suspend operator fun invoke(croppedBitmap: Bitmap): String? {
        val likeness =
            calculateLikenessUseCase.interpretBitmap(bitmap = croppedBitmap) ?: emptyArray()
        return getPersonsUseCase.invoke().value
            .map { person ->
                Pair(person.name, l2Norm(x1 = likeness.flatten(), x2 = person.likeness.flatten()))
            }
            .filter { it.second < 0.2f }
            .minByOrNull { pair ->
                // distance calculation between input face and saved faces
                // maxBy for cosineSim method and minBy for l2Norm method
                pair.second.also {
                    Log.d("FACE", "invoke: ${pair.first} :$it")
                }
            }?.first
    }

}
