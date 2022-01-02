package app.ilyos.domain.core

import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseVM : ViewModel() {

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(context, start, block)


    fun isUrlReachable(url: String) = liveData(Dispatchers.IO) {
        val connectionUrl = URL(url)
        try {
            val connection = connectionUrl.openConnection() as HttpURLConnection
            emit(connection.responseCode == 200)
        } catch (e: Exception) {
            emit(false)
        }
    }

    fun getVideoDuration(videoUrl: String) = liveData(Dispatchers.IO) {
        var duration = 0L
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoUrl)
            val time =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            duration = time?.toLong() ?: 0
        } catch (e: Exception) {

        } finally {
            retriever.release()
            retriever.close()
        }
        emit(duration)
    }


}