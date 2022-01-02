package app.ilyos.domain.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open abstract class BaseViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    abstract fun bind()
}