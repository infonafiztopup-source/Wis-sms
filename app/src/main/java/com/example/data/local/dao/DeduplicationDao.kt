package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.ProcessedMessageFingerprint
import com.example.data.local.entities.ProcessedTransactionEntity

@Dao
interface DeduplicationDao {

    // Layer A: Message Fingerprints
    @Query("SELECT * FROM processed_message_fingerprints WHERE fingerprint = :fingerprint LIMIT 1")
    suspend fun getMessageFingerprint(fingerprint: String): ProcessedMessageFingerprint?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessageFingerprint(fingerprint: ProcessedMessageFingerprint): Long

    // Layer B: Payment Transactions
    @Query("SELECT * FROM processed_transactions WHERE transactionKey = :key LIMIT 1")
    suspend fun getProcessedTransaction(key: String): ProcessedTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProcessedTransaction(transaction: ProcessedTransactionEntity): Long

    @Query("SELECT COUNT(*) FROM processed_transactions")
    suspend fun getTotalProcessedTransactionsCount(): Int

    @Query("DELETE FROM processed_message_fingerprints")
    suspend fun clearMessageFingerprints()

    @Query("DELETE FROM processed_transactions")
    suspend fun clearProcessedTransactions()
}
