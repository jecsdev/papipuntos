package com.jecsdev.papipuntos.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

private fun getAuthDatabaseBuilder(): RoomDatabase.Builder<AuthDatabase> {
    val dbFilePath = documentDirectory() + "/" + AUTH_DB_FILE
    return Room.databaseBuilder<AuthDatabase>(name = dbFilePath)
}

actual val platformAuthModule: Module = module {
    single<RoomDatabase.Builder<AuthDatabase>> { getAuthDatabaseBuilder() }
}
