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

@Database(
    entities = [AccountEntity::class, ProfileEntity::class, ActionEntity::class, RewardEntity::class, RedemptionEntity::class],
    version = 5,
)
@ConstructedBy(AuthDatabaseConstructor::class)
abstract class AuthDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun actionDao(): ActionDao
    abstract fun rewardDao(): RewardDao
}

// v1 -> v2 adds the persisted login flag. A real migration (not destructive) keeps
// any existing account/profiles across schema bumps during development.
val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE account ADD COLUMN sessionActive INTEGER NOT NULL DEFAULT 0")
    }
}

// v2 -> v3 makes the password columns nullable (remote Google/Apple accounts have none)
// and adds remoteUserId. SQLite can't relax NOT NULL in place, so the table is rebuilt.
// The new table intentionally omits the DEFAULT that MIGRATION_1_2 gave sessionActive, so
// it matches the Room-generated v3 schema (the Kotlin default is not a SQL default).
val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE account_new (" +
                "id INTEGER NOT NULL PRIMARY KEY, " +
                "email TEXT NOT NULL, " +
                "passwordHash TEXT, " +
                "passwordSalt TEXT, " +
                "sessionActive INTEGER NOT NULL, " +
                "remoteUserId TEXT)",
        )
        connection.execSQL(
            "INSERT INTO account_new (id, email, passwordHash, passwordSalt, sessionActive) " +
                "SELECT id, email, passwordHash, passwordSalt, sessionActive FROM account",
        )
        connection.execSQL("DROP TABLE account")
        connection.execSQL("ALTER TABLE account_new RENAME TO account")
    }
}

// v3 -> v4 introduces point requests. Existing accounts and profiles remain untouched.
val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE action_claim (" +
                "id TEXT NOT NULL PRIMARY KEY, " +
                "label TEXT NOT NULL, " +
                "points INTEGER NOT NULL, " +
                "emoji TEXT NOT NULL, " +
                "beneficiary TEXT NOT NULL, " +
                "approver TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "createdAtEpochMillis INTEGER NOT NULL, " +
                "resolvedAtEpochMillis INTEGER, " +
                "rejectionReason TEXT)",
        )
    }
}

// v4 -> v5 adds the local reward catalog and immutable redemption ledger. No existing
// account, profile, or action table is touched, so users retain every prior record.
val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "CREATE TABLE reward_catalog (id TEXT NOT NULL PRIMARY KEY, label TEXT NOT NULL, cost INTEGER NOT NULL, emoji TEXT NOT NULL)",
        )
        connection.execSQL(
            "CREATE TABLE reward_redemption (id TEXT NOT NULL PRIMARY KEY, player TEXT NOT NULL, rewardId TEXT NOT NULL, rewardLabel TEXT NOT NULL, rewardCost INTEGER NOT NULL, rewardEmoji TEXT NOT NULL, reason TEXT NOT NULL, createdAtEpochMillis INTEGER NOT NULL)",
        )
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
fun buildAuthDatabase(builder: RoomDatabase.Builder<AuthDatabase>): AuthDatabase = builder
    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.Default)
    .build()
