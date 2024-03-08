package com.biho.visageverify

import android.app.Application
import com.biho.visageverify.data.dataModule
import com.biho.visageverify.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VisageVerifyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@VisageVerifyApp)
            modules(
                listOf(
                    appModule(),
                    domainModule(),
                    dataModule()
                )
            )
        }
    }
}