package com.jecsdev.papipuntos

import android.app.Application
import com.jecsdev.papipuntos.data.db.AppContextHolder

/** Publishes the application Context so the Room builder can be created via Koin. */
class PapiPuntosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.context = this
    }
}
