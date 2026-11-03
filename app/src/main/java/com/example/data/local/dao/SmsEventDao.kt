package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.SmsEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsEventDao {
    @Query("SELECT * FROM sms_events ORDER BY receivedAt DESC")
    fun getAllEventsFlow(): Flow<List<SmsEventEntity>>

    @Query("SELECT * FROM sms_events ORDER BY receivedAt DESC LIMIT :limit")
    fun getRecentEventsFlow(limit: Int): Flow<List<SmsEventEntity>>

    @Query("SELECT * FROM sms_events WHERE eventId = :eventId LIMIT 1")
    suspend fun getEventById(eventId: String): SmsEventEntity?

    @Query("SELECT * FROM sms_events WHERE fingerprint = :fingerprint AND receivedAt > :sinceTime LIMIT 1")
    suspend fun findDuplicateEvent(fingerprint: String, sinceTime: Long): SmsEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SmsEventEntity)

    @Query("DELETE FROM sms_events WHERE eventId = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM sms_events")
    suspend fun deleteAllEvents()

    @Query("SELECT COUNT(*) FROM sms_events")
    fun getEventCountFlow(): Flow<Int>
}
