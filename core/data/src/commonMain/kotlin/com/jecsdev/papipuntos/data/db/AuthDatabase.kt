package com.jecsdev.papipuntos.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlinx.coroutines.Dispatchers

/** File name of the local auth SQLite database on every platform. */
const val AUTH_DB_FILE = "papipuntos_auth.db"

@Database(entities = [AccountEntity::class, ProfileEntity::class], version = 2)
@ConstructedBy(AuthDatabaseConstructor::class)
abstract class AuthDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
}

// v1 -> v2 adds the persisted login flag. A real migration (not destructive) keeps
// any existing account/profiles across schema bumps during development.
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE account ADD COLUMN sessionActive INTEGER NOT NULL DEFAULT 0")
    }
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
        .addMigrations(MIGRATION_1_2)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
