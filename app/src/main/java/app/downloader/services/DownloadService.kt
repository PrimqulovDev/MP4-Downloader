package app.downloader.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import app.db.video.VideoEntity
import app.downloader.R
import app.downloader.ui.activity.MainActivity
import app.ilyos.domain.repo.RepositoryImpl
import app.ilyos.domain.utils.Const.ACTION_PAUSE_DOWNLOADING
import app.ilyos.domain.utils.Const.ACTION_SHOW_DOWNLOADING_SCREEN
import app.ilyos.domain.utils.Const.ACTION_START_DOWNLOADING
import app.ilyos.domain.utils.Const.ACTION_STOP_DOWNLOADING
import app.ilyos.domain.utils.Const.FILES_DIR
import app.ilyos.domain.utils.Const.NOTIFICATION_CHANNEL_ID
import app.ilyos.domain.utils.Const.NOTIFICATION_CHANNEL_NAME
import app.ilyos.domain.utils.Const.NOTIFICATION_ID
import app.ilyos.domain.utils.Const.VIDEO_DATA
import app.ilyos.domain.utils.extensions.encodeAndCopyTo
import app.ilyos.domain.utils.extensions.externalOfflineDir
import app.ilyos.domain.utils.extensions.getCurrentTimeStamp
import app.model.*
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class DownloadService : LifecycleService() {

    @Inject
    lateinit var repository: RepositoryImpl

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (intent.action) {

                ACTION_START_DOWNLOADING -> {
                    video = intent.extras?.getSerializable(VIDEO_DATA) as? VideoData ?: video
                    if (video != null) {
                        if (status is Canceled) {
                            startForegroundService(video!!)
                        } else if (status is Paused) {
                            resumeDownloading()
                        }
                    }
                }

                ACTION_PAUSE_DOWNLOADING -> {
                    notificationManager.notify(NOTIFICATION_ID, getNotificationBuilder().build())
                    pauseDownloading()
                }

                ACTION_STOP_DOWNLOADING -> {
                    cancelService("")
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(video: VideoData) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        startForeground(NOTIFICATION_ID, getNotificationBuilder().build())
        startDownloading(video.url)
        setObservers()
        status = Downloading(0)
    }

    private fun resumeDownloading() {
        status = Downloading(downloadingPercentage)
        PRDownloader.resume(downloadingId)

    }

    private fun setObservers() {
        downloadingStatus.observe(this) {
            if (it is Downloading || it is Paused) {
                notificationManager.notify(NOTIFICATION_ID, getNotificationBuilder().build())
            }
        }
    }

    private fun cancelService(reason: String = "success") {
        PRDownloader.cancel(downloadingId)
        notificationManager.cancel(NOTIFICATION_ID)
        stopSelf()
        status = Canceled(reason)
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {

        val pauseIntent =
            PendingIntent.getService(this, 1, Intent(this, DownloadService::class.java).apply {
                action = ACTION_PAUSE_DOWNLOADING
            }, flagUpdateCurrent)

        val stopIntent =
            PendingIntent.getService(this, 1, Intent(this, DownloadService::class.java).apply {
                action = ACTION_STOP_DOWNLOADING
            }, flagUpdateCurrent)

        val resumeIntent =
            PendingIntent.getService(this, 1, Intent(this, DownloadService::class.java).apply {
                action = ACTION_START_DOWNLOADING
                putExtra(VIDEO_DATA, video)
            }, flagUpdateCurrent)

        var title = video?.title ?: "title of video"
        if (title.length >= 35) {
            title = "...${title.drop(title.length - 35)}"
        }

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setColor(ContextCompat.getColor(this, R.color.colorRed))
            .setSmallIcon(R.drawable.ic_download)
            .setContentTitle(title)
            .setProgress(100, downloadingPercentage, false)
            .setContentText("$downloadingPercentage %")
            .addAction(R.drawable.exo_icon_pause, "Cancel", stopIntent)

            .setContentIntent(getPendingMainActivityPendingIntent())

        if (status is Paused) {
            builder.addAction(R.drawable.exo_icon_pause, "Resume", resumeIntent)
        } else {
            builder.addAction(R.drawable.exo_icon_pause, "Pause", pauseIntent)
        }
        return builder
    }

    private fun startDownloading(fileUrl: String) {
        fileUrl.let {
            val filePath = getDir(FILES_DIR, Context.MODE_PRIVATE).absolutePath
            val fileName = "offline_video${getCurrentTimeStamp()}.mp4"

            if (it.isNotEmpty()) {
                downloadingId = PRDownloader.download(it, filePath, fileName).build()
                    .setOnStartOrResumeListener { }
                    .setOnProgressListener { progress ->
                        val percentage =
                            ((progress.currentBytes * 100) / progress.totalBytes).toInt()
                        if (percentage > downloadingPercentage && canUpdate) {
                            downloadingPercentage = percentage
                            status = Downloading(downloadingPercentage)
                            canUpdate = false
                            lifecycleScope.launch {
                                delay(3000L)
                                canUpdate = true
                            }
                        }
                    }
                    .start(object : OnDownloadListener {

                        override fun onDownloadComplete() {
                            val f = "$filePath/$fileName"
                            downloadingFile = File(f)
                            completeDownloading(downloadingFile!!)
                        }

                        override fun onError(error: Error?) {
                            status = Canceled(error?.serverErrorMessage ?: "Not found")
                            PRDownloader.cancel(downloadingId)
                            notificationManager.cancel(NOTIFICATION_ID)
                            stopSelf()
                        }

                    })

            }
        }
    }

    private fun completeDownloading(downloadedFile: File) {
        val timeStamp = getCurrentTimeStamp()
        val file = File(externalOfflineDir, timeStamp)

        CoroutineScope(Dispatchers.Main).launch {
            val duration = repository.getVideoDuration(downloadedFile.absolutePath)
            downloadedFile.encodeAndCopyTo(file)
            repository.saveVideo(
                VideoEntity(
                    remoteUrl = video!!.url,
                    localUrl = file.absolutePath,
                    title = video!!.title ?: file.name,
                    duration = duration,
                )
            )
            delay(1000L)
            cancelService()
            status = Success(file)
        }
    }

    private fun pauseDownloading() {
        status = Paused
        PRDownloader.pause(downloadingId)
    }


    private fun getPendingMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_DOWNLOADING_SCREEN
            it.putExtra(VIDEO_DATA, video)
        }, flagUpdateCurrent
    )


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManagerCompat) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private val mDownloadingStatus = MutableLiveData<Status?>()
        val downloadingStatus: LiveData<Status?> get() = mDownloadingStatus
        fun destroy() { mDownloadingStatus.value = null }
    }

    private val flagUpdateCurrent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
    } else {
        FLAG_UPDATE_CURRENT
    }

    private lateinit var notificationManager: NotificationManagerCompat
    private var video: VideoData? = null
    private var downloadingId = -1
    private var downloadingPercentage: Int = 0
    private var canUpdate = true
    private var downloadingFile: File? = null
    private var status: Status = Canceled("not started yet")
        private set(value) {
            mDownloadingStatus.postValue(value)
            field = value
        }
}