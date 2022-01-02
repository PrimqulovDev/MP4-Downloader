package app.db.video

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideosDao {

    @Query("SELECT * FROM videos_table")
    fun getAll(): Flow<List<VideoEntity>>

    @Query("DELETE FROM videos_table")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: VideoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: List<VideoEntity>): List<Long>
}