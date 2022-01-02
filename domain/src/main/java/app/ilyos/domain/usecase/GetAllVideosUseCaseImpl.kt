package app.ilyos.domain.usecase

import app.repo.Repository
import app.db.video.VideoEntity
import app.usecase.GetAllVideosUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllVideosUseCaseImpl @Inject constructor(
    private val repository: Repository
) : GetAllVideosUseCase {
    override fun invoke(): Flow<List<VideoEntity>> = repository.getAllVideos()
}