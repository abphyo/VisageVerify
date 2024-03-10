package com.biho.visageverify.domain.usecases

import com.biho.visageverify.data.model.Person
import com.biho.visageverify.data.realm.RealmRepoImpl
import kotlinx.coroutines.flow.StateFlow

class GetPersonsUseCase(
    private val realmRepoImpl: RealmRepoImpl
) {
    suspend operator fun invoke(): StateFlow<List<Person>> {
        return realmRepoImpl.getPersons()
    }

}