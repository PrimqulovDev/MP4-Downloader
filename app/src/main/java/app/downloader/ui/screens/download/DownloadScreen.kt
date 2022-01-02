package app.downloader.ui.screens.download

import android.content.Intent
import android.util.Patterns
import android.webkit.URLUtil
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import app.downloader.R
import app.downloader.databinding.ScreenDownloadBinding
import app.downloader.services.DownloadService
import app.downloader.ui.screens.Screens
import app.ilyos.domain.core.BaseFragment
import app.ilyos.domain.core.router
import app.ilyos.domain.utils.Const.ACTION_PAUSE_DOWNLOADING
import app.ilyos.domain.utils.Const.ACTION_START_DOWNLOADING
import app.ilyos.domain.utils.Const.ACTION_STOP_DOWNLOADING
import app.ilyos.domain.utils.Const.VIDEO_DATA
import app.ilyos.domain.utils.extensions.*
import app.ilyos.domain.utils.millSecondsToLabel
import app.ilyos.domain.viewbinding.viewBinding
import app.model.*
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DownloadScreen : BaseFragment(R.layout.screen_download) {

    private val viewModel: DownloadVM by viewModels()
    private val binding by viewBinding(ScreenDownloadBinding::bind)

    override fun initialize() {
        videoData = arguments?.getSerializable(VIDEO_DATA) as? VideoData
        videoTitle = videoData?.title ?: ""
        videoUrl = videoData?.url ?: ""
        showDetails()

        binding.btCheck.setOnClickListener {
            videoUrl = binding.inputUrl.stringText
            videoTitle = URLUtil.guessFileName(videoUrl, null, null)
            if (videoTitle.isEmpty()) videoTitle = "no title video"

            hideKeyboard()
            if (checkUrl(videoUrl)) {
                isLoading = true
                viewModel.isUrlReachable(videoUrl).observe(viewLifecycleOwner) { reachable ->
                    isLoading = false
                    if (reachable) {
                        showDetails()
                        sendCommandToService(
                            ACTION_START_DOWNLOADING,
                            VideoData(videoUrl, videoTitle, null)
                        )
                    } else {
                        message("URL is not reachable")
                    }
                }
            }
        }

        binding.btPaste.setOnClickListener {
            binding.inputUrl.setText(requireContext().getTextFromClipboard())
        }

        DownloadService.apply {
            downloadingStatus.observe(viewLifecycleOwner) { status ->
                if (status != null) {
                    when (status) {
                        is Downloading -> {
                            with(binding) {
                                llVideo.visible()
                                btCheck.gone()
                                btPaste.gone()
                                inputUrl.disable()
                                btCancel.visible()
                                btPause.visible()
                                btResume.gone()
                                tvPercentage.text = ("${status.percentage}%")
                                tvStatus.text = getString(R.string.label_downloading)
                                pbDownloading.progress = status.percentage
                            }
                        }

                        is Canceled -> {
                            with(binding) {
                                llVideo.gone()
                                btCheck.visible()
                                btPaste.visible()
                                inputUrl.enable()
                                message(status.message)
                                tvStatus.text = getString(R.string.label_canceled)
                            }
                        }

                        is Success -> {
                            binding.inputUrl.setText("")
                            destroy()
                        }

                        Paused -> {
                            with(binding) {
                                tvStatus.text = getString(R.string.label_paused)
                                btCancel.visible()
                                btPause.gone()
                                btResume.visible()
                            }
                        }
                    }
                }
            }
        }

        binding.btCancel.setOnClickListener {
            sendCommandToService(ACTION_STOP_DOWNLOADING)
        }

        binding.btPause.setOnClickListener {
            sendCommandToService(ACTION_PAUSE_DOWNLOADING)

        }

        binding.btResume.setOnClickListener {
            sendCommandToService(ACTION_START_DOWNLOADING)
        }

        binding.btPlay.setOnClickListener {
            navigateTo(Screens.openMedia(videoUrl))
        }

    }


    private fun showDetails() {
        Glide.with(requireView())
            .load(videoUrl)
            .error(R.color.colorGreyB3)
            .placeholder(R.color.colorGreyB3)
            .into(binding.ivThumbnail)

        binding.tvTitle.text = videoTitle
        binding.inputUrl.setText(videoUrl)
        viewModel.getVideoDuration(videoUrl).observe(viewLifecycleOwner) {
            binding.tvDuration.text = it.millSecondsToLabel()
        }
    }

    private fun checkUrl(url: String): Boolean {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            message("URL is not valid")
            return false
        }

        if (!url.lowercase(Locale.getDefault()).endsWith(".mp4")) {
            message("there is no mp4 file in this url")
            return false
        }
        return true
    }

    private fun sendCommandToService(command: String, videoData: VideoData? = null) {
        Intent(requireContext(), DownloadService::class.java).also {
            it.action = command
            it.putExtra(VIDEO_DATA, videoData)
            requireContext().startService(it)
        }
    }

    companion object {
        fun withArgs(videoData: VideoData?) = DownloadScreen().apply {
            arguments = bundleOf(VIDEO_DATA to videoData)
        }
    }


    private var videoTitle = ""
    private var videoUrl = ""
    private var videoData: VideoData? = null

}