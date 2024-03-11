package com.biho.visageverify.domain.usecases

import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import com.biho.visageverify.data.TfLiteInterpreter
import com.biho.visageverify.data.model.Person
import com.biho.visageverify.data.model.TfLitePreferences
import com.biho.visageverify.data.realm.RealmRepoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class CalculateLikenessUseCase(
    private val tfLiteStore: DataStore<TfLitePreferences>,
    private val scope: CoroutineScope,
    private val tfLiteInterpreter: TfLiteInterpreter
) {
    fun interpretBitmap(bitmap: Bitmap): Array<FloatArray>? {
        val tfLitePrefs = tfLiteStore.data
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = TfLitePreferences()
            ).value

        tfLiteInterpreter.setUpInterpreter(
            threadCount = tfLitePrefs.threadCount,
            processorDelegate = tfLitePrefs.processorDelegate,
            model = tfLitePrefs.model
        )
        return try {
            tfLiteInterpreter.interpret(bitmap = bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}