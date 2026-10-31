package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reader_reviews ORDER BY timestamp DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reader_reviews WHERE bookId = :bookId ORDER BY timestamp DESC")
    fun getReviewsForBook(bookId: Long): Flow<List<Review>>

    @Query("SELECT COUNT(*) FROM reader_reviews")
    suspend fun getReviewCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<Review>)

    @Query("DELETE FROM reader_reviews WHERE reviewId = :reviewId")
    suspend fun deleteReview(reviewId: Long)
}
