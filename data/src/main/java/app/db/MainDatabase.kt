package app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import app.db.video.VideoEntity
import app.db.video.VideosDao

@Database(
    entities = [
        VideoEntity::class
    ], version = 1
)
abstract class MainDatabase : RoomDatabase() {
    abstract val videosDao: VideosDao
}