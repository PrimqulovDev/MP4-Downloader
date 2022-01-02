package app.downloader.ui.activity

import android.content.Intent
import android.os.Bundle
import app.downloader.ui.screens.Screens
import app.ilyos.domain.core.BaseActivity
import app.ilyos.domain.utils.Const.ACTION_SHOW_DOWNLOADING_SCREEN
import app.ilyos.domain.utils.Const.VIDEO_DATA
import app.model.VideoData
import com.github.terrakok.cicerone.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override val rootScreen: Screen = Screens.myVideos()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        navigateIfNeed(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        navigateIfNeed(intent)
        super.onNewIntent(intent)
    }

    private fun navigateIfNeed(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_DOWNLOADING_SCREEN) {
            val videoData = intent.extras?.getSerializable(VIDEO_DATA) as? VideoData
            router.navigateTo(Screens.download(videoData))
        }

    }

}