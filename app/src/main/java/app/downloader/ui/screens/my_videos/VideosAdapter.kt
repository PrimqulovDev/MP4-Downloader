package app.downloader.ui.screens.my_videos

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import app.db.video.VideoEntity
import app.downloader.R
import app.ilyos.domain.rv.BaseAdapter
import app.ilyos.domain.rv.BaseViewHolder
import app.ilyos.domain.utils.millSecondsToLabel
import com.bumptech.glide.Glide

class VideosAdapter : BaseAdapter<VideoEntity>() {

    override fun getItemViewType(position: Int) = R.layout.item_video_local
    override fun onCreateViewHolder(view: View, viewType: Int) = ViewHolder(view)

    inner class ViewHolder(v: View) : BaseViewHolder(v) {
        private val ivThumbnail: AppCompatImageView = v.findViewById(R.id.videoThumbnail)
        private val tvTitle: AppCompatTextView = v.findViewById(R.id.tvTitle)
        private val tvDuration: AppCompatTextView = v.findViewById(R.id.tvDuration)

        override fun bind() {
            val d = getItem(absoluteAdapterPosition)
            tvTitle.text = d.title
            tvDuration.text = d.duration.millSecondsToLabel()
            Glide.with(itemView)
                .load(d.decodedUrl)
                .placeholder(R.color.black)
                .error(R.color.black)
                .into(ivThumbnail)
        }
    }
}