package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AppSettingsDao
import com.example.data.local.dao.DeduplicationDao
import com.example.data.local.dao.DeliveryLogDao
import com.example.data.local.dao.EndpointDao
import com.example.data.local.dao.SmsEventDao
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.ProcessedMessageFingerprint
import com.example.data.local.entities.ProcessedTransactionEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.data.local.entities.WebhookEndpointEntity

@Database(
    entities = [
        WebhookEndpointEntity::class,
        SmsEventEntity::class,
        DeliveryLogEntity::class,
        AppSettingsEntity::class,
        ProcessedMessageFingerprint::class,
        ProcessedTransactionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class WisfyDatabase : RoomDatabase() {
    abstract fun endpointDao(): EndpointDao
    abstract fun smsEventDao(): SmsEventDao
    abstract fun deliveryLogDao(): DeliveryLogDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun deduplicationDao(): DeduplicationDao

    companion object {
        @Volatile
        private var INSTANCE: WisfyDatabase? = null

        fun getDatabase(context: Context): WisfyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WisfyDatabase::class.java,
                    "wisfy_sms.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
