package app.downloader.ui.screens.my_videos

import androidx.fragment.app.viewModels
import app.db.video.VideoEntity
import app.downloader.R
import app.downloader.databinding.ScreenMyVideosBinding
import app.downloader.ui.screens.Screens
import app.ilyos.domain.core.BaseFragment
import app.ilyos.domain.utils.extensions.decodeAndCopyTo
import app.ilyos.domain.utils.extensions.getCurrentTimeStamp
import app.ilyos.domain.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.File

@AndroidEntryPoint
class MyVideosScreen : BaseFragment(R.layout.screen_my_videos) {

    private val viewModel: MyVideosVM by viewModels()
    private val binding by viewBinding(ScreenMyVideosBinding::bind)
    private val videosAdapter: VideosAdapter by lazy { VideosAdapter() }

    override fun initialize() {
        binding.rvVideos.adapter = videosAdapter

        videosAdapter.setOnIteClickListener { video ->
            if (video.decodedUrl.isNotEmpty()) {
                navigateTo(Screens.openMedia(video.decodedUrl))
            }
        }

        binding.btDownload.setOnClickListener {
            navigateTo(Screens.download())
        }

        viewModel.videosLiveData.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                if (videosAdapter.currentList != it) {
                    decodeAllVideos(it)
                }
            }
        }
    }

    private fun decodeAllVideos(videos: List<VideoEntity>) {
        isLoading = true
        val tasks = mutableListOf<Deferred<Unit>>()
        CoroutineScope(Job() + Main).launch {
            coroutineScope {
                videos.forEach {
                    if (it.decodedUrl.isEmpty()) {
                        tasks.add(async { decodeVideo(it)})
                    }
                }
                tasks.awaitAll()
            }
        }.invokeOnCompletion {
            videosAdapter.submitList(videos.filter { it.available })
            isLoading = false
        }

    }

    private suspend fun decodeVideo(video: VideoEntity) = withContext(IO) {
        val filename = "${getCurrentTimeStamp()}${video.title}.mp4"
        val out = File(requireContext().cacheDir, filename)
        try {
            val input = File(video.localUrl)
            input.decodeAndCopyTo(out)
            video.decodedUrl = out.absolutePath
        } catch (e: Exception) {
            video.available = false
        }
    }
}