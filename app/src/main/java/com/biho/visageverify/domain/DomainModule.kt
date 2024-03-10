package com.biho.visageverify.domain

import com.biho.visageverify.domain.usecases.CalculateLikenessUseCase
import com.biho.visageverify.domain.usecases.FaceDetectionUseCase
import com.biho.visageverify.domain.usecases.GetPersonsUseCase
import com.biho.visageverify.domain.usecases.ValidatePersonUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

fun domainModule() = module {
    factoryOf(::FaceDetectionUseCase)
    factoryOf(::CalculateLikenessUseCase)
    factoryOf(::GetPersonsUseCase)
    factoryOf(::ValidatePersonUseCase)
}