package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.DeliveryLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryLogDao {
    @Query("SELECT * FROM delivery_logs ORDER BY createdAt DESC")
    fun getAllLogsFlow(): Flow<List<DeliveryLogEntity>>

    @Query("SELECT * FROM delivery_logs ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentLogsFlow(limit: Int): Flow<List<DeliveryLogEntity>>

    @Query("SELECT * FROM delivery_logs WHERE status = :status ORDER BY createdAt DESC")
    fun getLogsByStatusFlow(status: String): Flow<List<DeliveryLogEntity>>

    @Query("SELECT * FROM delivery_logs WHERE id = :id LIMIT 1")
    suspend fun getLogById(id: Long): DeliveryLogEntity?

    @Query("SELECT * FROM delivery_logs WHERE eventId = :eventId")
    suspend fun getLogsForEvent(eventId: String): List<DeliveryLogEntity>

    @Query("SELECT * FROM delivery_logs WHERE status IN ('failed', 'retry_scheduled')")
    suspend fun getEligibleRetryLogs(): List<DeliveryLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DeliveryLogEntity): Long

    @Update
    suspend fun updateLog(log: DeliveryLogEntity)

    @Query("UPDATE delivery_logs SET status = :status, httpStatusCode = :code, latencyMs = :latency, responseBodySnippet = :response, errorMessage = :error, deliveredAt = :deliveredAt, attemptCount = :attemptCount WHERE id = :id")
    suspend fun updateLogResult(
        id: Long,
        status: String,
        code: Int?,
        latency: Long,
        response: String?,
        error: String?,
        deliveredAt: Long?,
        attemptCount: Int
    )

    @Query("SELECT COUNT(*) FROM delivery_logs WHERE status = 'delivered'")
    fun getDeliveredCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM delivery_logs WHERE status IN ('queued', 'sending', 'retry_scheduled')")
    fun getQueuedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM delivery_logs WHERE status = 'failed'")
    fun getFailedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM delivery_logs")
    fun getTotalLogsCountFlow(): Flow<Int>

    @Query("DELETE FROM delivery_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM delivery_logs")
    suspend fun deleteAllLogs()
}
