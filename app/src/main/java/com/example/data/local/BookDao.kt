package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books ORDER BY createdAt DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookById(id: Long): Book?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<Book>)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("UPDATE books SET isUnlocked = 1 WHERE id = :bookId")
    suspend fun unlockBook(bookId: Long)

    @Query("UPDATE books SET priceDzd = :priceDzd, priceUsdt = :priceUsdt, isFree = :isFree WHERE id = :bookId")
    suspend fun updateBookPrice(bookId: Long, priceDzd: Double, priceUsdt: Double, isFree: Boolean)

    @Query("SELECT COUNT(*) FROM books")
    suspend fun getBookCount(): Int
}
