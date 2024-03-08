package com.biho.visageverify.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun domainModule() = module {
    singleOf(::FaceDetectionUseCase)
}