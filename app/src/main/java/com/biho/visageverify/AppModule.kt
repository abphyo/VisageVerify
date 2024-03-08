package com.biho.visageverify

import com.biho.visageverify.presentation.DetectViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    single { SupervisorJob() }
    factory { CoroutineScope(context = Dispatchers.IO + get<CompletableJob>()) }
    viewModelOf(::MainViewModel)
    viewModelOf(::DetectViewModel)
}