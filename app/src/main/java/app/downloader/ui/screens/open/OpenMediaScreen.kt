package app.downloader.ui.screens.open

import androidx.core.os.bundleOf
import app.downloader.R
import app.downloader.databinding.CustomPlayerControllerBinding
import app.downloader.databinding.ScreenOpenMediaBinding
import app.ilyos.domain.core.BaseFragment
import app.ilyos.domain.utils.extensions.gone
import app.ilyos.domain.utils.extensions.visible
import app.ilyos.domain.viewbinding.viewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class OpenMediaScreen : BaseFragment(R.layout.screen_open_media) {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val binding by viewBinding(ScreenOpenMediaBinding::bind)
    private val mediaControllerBinding by viewBinding(CustomPlayerControllerBinding::bind)

    private var url: String? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun initialize() {
        playerView = requireView().findViewById(R.id.video_view)
        url = arguments?.getString(MEDIA_URL)


        if (url != null) {
            binding.bottomBar.visible()
            initializePlayer()
        }

        binding.btBack.setOnClickListener {
            exit()
        }
    }


    private fun initializePlayer() {

        player = ExoPlayer.Builder(requireContext()).build()
        playerView.player = player

        val extractorsFactory = DefaultExtractorsFactory()

        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
            requireContext()
        )

        val media = ProgressiveMediaSource.Factory(mediaDataSourceFactory, extractorsFactory)
            .createMediaSource(MediaItem.fromUri(url!!))

        player!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    mediaControllerBinding.pbPlayer.visible()
                } else if (playbackState == Player.STATE_READY) {
                    mediaControllerBinding.pbPlayer.gone()
                }
                super.onPlaybackStateChanged(playbackState)
            }
        })

        player!!.playWhenReady = true
        player!!.setMediaSource(media)
        player?.seekTo(currentWindow, playbackPosition)
        player!!.prepare()
    }


    private fun releasePlayer() {
        if (player != null) {
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentMediaItemIndex
            player!!.playWhenReady = false
            player!!.release()
            player!!.stop()
            player = null
        }
    }

    override fun onDestroyView() {
        releasePlayer()
        super.onDestroyView()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    override fun onPause() {
        releasePlayer()
        super.onPause()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    companion object {
        fun withArgs(url: String) = OpenMediaScreen().apply {
            arguments = bundleOf(MEDIA_URL to url)
        }
        private const val MEDIA_URL = "MEDIA_URL"
    }
}