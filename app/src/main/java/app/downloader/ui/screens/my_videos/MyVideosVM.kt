package app.downloader.ui.screens.my_videos

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import app.db.video.VideoEntity
import app.usecase.GetAllVideosUseCase
import app.ilyos.domain.core.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyVideosVM @Inject constructor(
    getAllVideos: GetAllVideosUseCase
) : BaseVM() {

    val videosLiveData: LiveData<List<VideoEntity>> = getAllVideos().asLiveData()


}