package com.biho.visageverify.domain.usecases

import android.graphics.Bitmap
import com.biho.visageverify.data.model.Person
import com.biho.visageverify.data.realm.RealmRepoImpl

class SavePersonUseCase(
    private val realmRepoImpl: RealmRepoImpl,
    private val calculateLikenessUseCase: CalculateLikenessUseCase
) {
    suspend operator fun invoke(croppedBitmap: Bitmap, name: String): Result<Unit> {
        val likeness = calculateLikenessUseCase.interpretBitmap(croppedBitmap)
        return when {
            likeness != null -> realmRepoImpl.addPerson(
                Person(
                    name = name,
                    picture = croppedBitmap,
                    likeness = likeness
                )
            )
            else -> Result.failure(Throwable("likeness calculation failed"))
        }
    }
}