package com.biho.visageverify.data.realm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.biho.visageverify.data.model.Person
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.io.ByteArrayOutputStream

class RealmPerson: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _name: String = ""
    var _picture: ByteArray = ByteArray(0)
    var _likeness: RealmList<RealmFloatArray> = realmListOf()
}

class RealmFloatArray: EmbeddedRealmObject {
    var _values: RealmList<Float> = realmListOf()
}

fun Person.toRealm(): RealmPerson {
    return RealmPerson().apply {
        _name = name
        _picture = picture.toByteArray()
        _likeness.addAll(
            likeness.map {
                RealmFloatArray().apply {
                    _values.addAll(it.toList())
                }
            }
        )
    }
}

fun RealmPerson.toDomain(): Person {
    return Person(
        name = _name,
        picture = _picture.toBitmap(),
        likeness = _likeness.map { realmArray ->
            realmArray._values.toFloatArray()
        }.toTypedArray()
    )
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}