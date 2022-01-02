package app.ilyos.domain.repo

import android.media.MediaMetadataRetriever
import app.db.video.VideoEntity
import app.db.video.VideosDao
import app.repo.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val videosDao: VideosDao
) : Repository {

    override fun getAllVideos(): Flow<List<VideoEntity>> = videosDao.getAll()

    override suspend fun saveVideo(video: VideoEntity): Long =
        withContext(IO) { videosDao.insert(video) }

    override suspend fun saveVideo(videos: List<VideoEntity>): List<Long> =
        withContext(IO) { videosDao.insert(videos) }

    suspend fun getVideoDuration(videoUrl: String) = withContext(IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoUrl)
            val time =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            time?.toLong() ?: 0
        } catch (e: Exception) {
            0
        } finally {
            retriever.release()
            retriever.close()
        }
    }

}