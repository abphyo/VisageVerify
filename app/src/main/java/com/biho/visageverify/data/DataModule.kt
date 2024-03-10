package com.biho.visageverify.data

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.biho.visageverify.data.realm.RealmFloatArray
import com.biho.visageverify.data.realm.RealmPerson
import com.biho.visageverify.data.realm.RealmRepoImpl
import com.biho.visageverify.data.utils.TfLitePrefSerializer
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun dataModule() = module {
    single {
        DataStoreFactory.create(
            serializer = TfLitePrefSerializer,
            produceFile = { get<Context>().dataStoreFile("TfLite-prefs.json") },
            corruptionHandler = null,
            scope = get<CoroutineScope>()
        )
    }
    single<FaceDetector> {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE) // skips landmark mapping
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE) // skips facial expressions and other classification such as wink
            .build()

        FaceDetection.getClient(options)
    }
    single {
        Realm.open(
            configuration = RealmConfiguration.create(
                schema = setOf(
                    RealmPerson::class,
                    RealmFloatArray::class
                )
            )
        )
    }
    singleOf(::TfLiteInterpreter)
    singleOf(::RealmRepoImpl)
}