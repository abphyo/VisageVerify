package com.biho.visageverify.domain.usecases

import android.graphics.Bitmap
import com.biho.visageverify.data.model.Person
import com.biho.visageverify.data.realm.RealmRepoImpl

class SavePersonUseCase(
    private val realmRepoImpl: RealmRepoImpl,
    private val calculateLikenessUseCase: CalculateLikenessUseCase
) {
    suspend operator fun invoke(croppedBitmap: Bitmap, name: String): Result<Unit> {
        var likeness: Array<FloatArray>? = null
        var throwable = Throwable()
        calculateLikenessUseCase.interpretBitmap(croppedBitmap).onSuccess {
            likeness = it
        }.onFailure {
            throwable = it
        }
        return when {
            likeness != null -> realmRepoImpl.addPerson(
                Person(
                    name = name,
                    likeness = likeness!!
                )
            )
            else -> Result.failure(throwable)
        }
    }
}