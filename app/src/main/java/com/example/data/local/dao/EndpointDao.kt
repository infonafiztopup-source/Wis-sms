package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.WebhookEndpointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EndpointDao {
    @Query("SELECT * FROM webhook_endpoints ORDER BY createdAt DESC")
    fun getAllEndpointsFlow(): Flow<List<WebhookEndpointEntity>>

    @Query("SELECT * FROM webhook_endpoints ORDER BY createdAt DESC")
    suspend fun getAllEndpoints(): List<WebhookEndpointEntity>

    @Query("SELECT * FROM webhook_endpoints WHERE enabled = 1")
    suspend fun getEnabledEndpoints(): List<WebhookEndpointEntity>

    @Query("SELECT * FROM webhook_endpoints WHERE id = :id LIMIT 1")
    suspend fun getEndpointById(id: Long): WebhookEndpointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEndpoint(endpoint: WebhookEndpointEntity): Long

    @Update
    suspend fun updateEndpoint(endpoint: WebhookEndpointEntity)

    @Delete
    suspend fun deleteEndpoint(endpoint: WebhookEndpointEntity)

    @Query("DELETE FROM webhook_endpoints WHERE id = :id")
    suspend fun deleteEndpointById(id: Long)

    @Query("SELECT COUNT(*) FROM webhook_endpoints")
    fun getEndpointCountFlow(): Flow<Int>
}
