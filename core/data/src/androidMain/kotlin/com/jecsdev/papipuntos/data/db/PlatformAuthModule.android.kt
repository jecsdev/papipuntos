package com.jecsdev.papipuntos.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Holds the application [Context] so the Room builder can be created without wiring
 * Koin's androidContext. The Android Application sets this in onCreate, which runs
 * before any Koin resolution.
 */
object AppContextHolder {
    lateinit var context: Context
}

private fun getAuthDatabaseBuilder(context: Context): RoomDatabase.Builder<AuthDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(AUTH_DB_FILE)
    return Room.databaseBuilder<AuthDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}

actual val platformAuthModule: Module = module {
    single<RoomDatabase.Builder<AuthDatabase>> { getAuthDatabaseBuilder(AppContextHolder.context) }
}
