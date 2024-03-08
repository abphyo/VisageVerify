package com.biho.visageverify.data.utils

import androidx.datastore.core.Serializer
import com.biho.visageverify.data.model.TfLitePreferences
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object TfLitePrefSerializer: Serializer<TfLitePreferences> {

    override val defaultValue: TfLitePreferences
        get() = TfLitePreferences()

    override suspend fun readFrom(input: InputStream): TfLitePreferences {
        return try {
            Json.decodeFromString(
                deserializer = TfLitePreferences.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: TfLitePreferences, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = TfLitePreferences.serializer(),
                value = t
            ).toByteArray()
        )
    }
}