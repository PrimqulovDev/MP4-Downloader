package app.downloader.ui.screens

import app.downloader.ui.screens.open.OpenMediaScreen
import app.downloader.ui.screens.download.DownloadScreen
import app.downloader.ui.screens.my_videos.MyVideosScreen
import app.model.VideoData
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {
    fun myVideos() = FragmentScreen { MyVideosScreen() }
    fun download() = FragmentScreen { DownloadScreen() }
    fun download(videoData: VideoData?) = FragmentScreen { DownloadScreen.withArgs(videoData) }
    fun openMedia(mediaUrl: String) = FragmentScreen { OpenMediaScreen.withArgs(mediaUrl) }
}