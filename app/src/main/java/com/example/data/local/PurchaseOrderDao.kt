package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PurchaseOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseOrderDao {
    @Query("SELECT * FROM purchase_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<PurchaseOrder>>

    @Query("SELECT COUNT(*) FROM purchase_orders")
    suspend fun getOrderCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: PurchaseOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<PurchaseOrder>)

    @Query("UPDATE purchase_orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("DELETE FROM purchase_orders WHERE orderId = :orderId")
    suspend fun deleteOrder(orderId: Long)
}
