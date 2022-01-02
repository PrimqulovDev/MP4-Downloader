package app.repo

import app.db.video.VideoEntity
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getAllVideos(): Flow<List<VideoEntity>>
    suspend fun saveVideo(video: VideoEntity): Long
    suspend fun saveVideo(videos: List<VideoEntity>): List<Long>

}