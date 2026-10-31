package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PaymentSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentSettingsDao {
    @Query("SELECT * FROM payment_settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<PaymentSettings?>

    @Query("SELECT * FROM payment_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): PaymentSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: PaymentSettings)
}
