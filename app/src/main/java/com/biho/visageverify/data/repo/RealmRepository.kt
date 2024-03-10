package com.biho.visageverify.data.repo

import com.biho.visageverify.data.model.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RealmRepository {
    suspend fun addPerson(person: Person): Result<Unit>
    suspend fun getPersons(): StateFlow<List<Person>>
}