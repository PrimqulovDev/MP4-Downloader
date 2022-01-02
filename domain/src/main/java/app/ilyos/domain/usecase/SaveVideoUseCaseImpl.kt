package app.ilyos.domain.usecase

import app.repo.Repository
import app.db.video.VideoEntity
import app.usecase.SaveVideoUseCase
import javax.inject.Inject

class SaveVideoUseCaseImpl @Inject constructor(
    private val repository: Repository
) : SaveVideoUseCase {
    override suspend fun invoke(video: VideoEntity): Long = repository.saveVideo(video)
}