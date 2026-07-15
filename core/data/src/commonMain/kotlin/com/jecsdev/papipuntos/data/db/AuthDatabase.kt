package com.jecsdev.papipuntos.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

/** File name of the local auth SQLite database on every platform. */
const val AUTH_DB_FILE = "papipuntos_auth.db"

@Database(entities = [AccountEntity::class, ProfileEntity::class], version = 1)
@ConstructedBy(AuthDatabaseConstructor::class)
abstract class AuthDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
}

// KSP generates the actual for this expect object per platform; the suppression is
// the documented Room KMP requirement (the compiler cannot see the generated actual).
@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object AuthDatabaseConstructor : RoomDatabaseConstructor<AuthDatabase> {
    override fun initialize(): AuthDatabase
}

/**
 * Finishes a platform-provided [builder] with the bundled driver and a query context.
 * Uses [Dispatchers.Default] because Dispatchers.IO is not available on Kotlin/Native;
 * the auth store is tiny and rarely touched, so the default pool is fine here.
 */
fun buildAuthDatabase(builder: RoomDatabase.Builder<AuthDatabase>): AuthDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
