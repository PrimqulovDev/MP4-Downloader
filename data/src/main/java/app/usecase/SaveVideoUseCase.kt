package app.usecase

import app.db.video.VideoEntity

interface SaveVideoUseCase {
    suspend operator fun invoke(video: VideoEntity): Long
}