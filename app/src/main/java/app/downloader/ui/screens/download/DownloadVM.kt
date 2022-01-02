package app.downloader.ui.screens.download

import app.db.video.VideoEntity
import app.usecase.SaveVideoUseCase
import app.ilyos.domain.core.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloadVM @Inject constructor(
    val save: SaveVideoUseCase
) : BaseVM() {
    fun saveVideo(video: VideoEntity) = launch { save(video) }
}