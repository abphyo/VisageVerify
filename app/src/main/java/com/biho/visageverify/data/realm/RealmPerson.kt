package com.biho.visageverify.data.realm

import com.biho.visageverify.data.model.Person
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class RealmPerson: RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _name: String = ""
    var _likeness: RealmList<RealmFloatArray> = realmListOf()
}

class RealmFloatArray: EmbeddedRealmObject {
    var _values: RealmList<Float> = realmListOf()
}

fun Person.toRealm(): RealmPerson {
    return RealmPerson().apply {
        _name = name
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
        likeness = _likeness.map { realmArray ->
            realmArray._values.toFloatArray()
        }.toTypedArray()
    )
}