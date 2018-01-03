package fr.guddy.kandroidmodular

import android.app.Application
import fr.guddy.kandroidmodular.di.netModule
import org.koin.android.ext.android.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(
                this,
                listOf(netModule)
        )
    }
}