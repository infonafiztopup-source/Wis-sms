package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    @Query("UPDATE app_settings SET backgroundServiceActive = :active WHERE id = 1")
    suspend fun setServiceActive(active: Boolean)

    @Query("UPDATE app_settings SET hasCompletedOnboarding = :completed WHERE id = 1")
    suspend fun setOnboardingCompleted(completed: Boolean)

    @Query("UPDATE app_settings SET maskSmsBodyInLogs = :mask WHERE id = 1")
    suspend fun setMaskSmsBody(mask: Boolean)

    @Query("UPDATE app_settings SET forwardRawMessageBody = :forward WHERE id = 1")
    suspend fun setForwardRawBody(forward: Boolean)

    @Query("UPDATE app_settings SET autoFilterOtp = :filter WHERE id = 1")
    suspend fun setAutoFilterOtp(filter: Boolean)

    @Query("UPDATE app_settings SET retryCount = :retries, timeoutSeconds = :timeout WHERE id = 1")
    suspend fun setRetryAndTimeout(retries: Int, timeout: Int)

    @Query("UPDATE app_settings SET themeMode = :mode WHERE id = 1")
    suspend fun setThemeMode(mode: String)
}
