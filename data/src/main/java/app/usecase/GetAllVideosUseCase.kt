package app.usecase

import app.db.video.VideoEntity
import kotlinx.coroutines.flow.Flow

interface GetAllVideosUseCase {
    operator fun invoke(): Flow<List<VideoEntity>>
}