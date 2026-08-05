package com.jecsdev.papipuntos.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

/** Data access for the local auth store: one account plus the two profiles. */
@Dao
interface AuthDao {
    @Query("SELECT * FROM account LIMIT 1")
    suspend fun getAccount(): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAccount(account: AccountEntity)

    // Flips the persisted login flag on the single account row without touching the
    // stored secrets: true after a successful login/sign-up, false after logout.
    @Query("UPDATE account SET sessionActive = :active WHERE id = 0")
    suspend fun setSessionActive(active: Boolean)

    @Query("SELECT * FROM profile")
    suspend fun getProfiles(): List<ProfileEntity>

    @Query("SELECT * FROM profile WHERE player = :player LIMIT 1")
    suspend fun getProfile(player: String): ProfileEntity?

    @Upsert
    suspend fun upsertProfiles(profiles: List<ProfileEntity>)
}
