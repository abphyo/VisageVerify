package com.biho.visageverify.data.realm

import com.biho.visageverify.data.model.Person
import com.biho.visageverify.data.repo.RealmRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class RealmRepoImpl(
    private val realm: Realm,
    private val ioScope: CoroutineScope
) : RealmRepository {

    override suspend fun addPerson(person: Person): Result<Unit> {
        return try {
            realm.write {
                copyToRealm(person.toRealm(), UpdatePolicy.ALL)
                println("save person: $person")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(Throwable(e.message))
        }
    }

    override suspend fun getPersons(): StateFlow<List<Person>> {
        val personFlow = realm.query<RealmPerson>().asFlow().map { results ->
            println("saved person: $results")
            results.list.map { it.toDomain() }
        }
        return personFlow.stateIn(
            scope = ioScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = runBlocking {
                personFlow.first()
            }
        )
    }

}