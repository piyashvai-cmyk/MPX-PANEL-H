package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.LicenseKey
import kotlinx.coroutines.flow.Flow

@Dao
interface LicenseKeyDao {
    @Query("SELECT * FROM license_keys ORDER BY createdAt DESC")
    fun getAllKeys(): Flow<List<LicenseKey>>

    @Query("SELECT * FROM license_keys WHERE `key` = :key LIMIT 1")
    suspend fun getKey(key: String): LicenseKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: LicenseKey)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: List<LicenseKey>)

    @Update
    suspend fun updateKey(key: LicenseKey)

    @Query("DELETE FROM license_keys WHERE `key` = :key")
    suspend fun deleteKey(key: String)

    @Query("DELETE FROM license_keys")
    suspend fun deleteAllKeys()

    @Query("SELECT COUNT(*) FROM license_keys")
    suspend fun getKeyCount(): Int
}
