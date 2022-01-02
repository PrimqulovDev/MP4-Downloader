package app.ilyos.domain.rv

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import app.ilyos.domain.utils.extensions.inflate
import app.model.BaseItem
import app.model.getBaseItemDiffUtil

abstract class BaseAdapter<T : BaseItem> : ListAdapter<T, BaseViewHolder>(getBaseItemDiffUtil()) {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) = holder.bind()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        onCreateViewHolder(parent.inflate(viewType), viewType).apply {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(getItem(absoluteAdapterPosition))
            }
        }

    private var onItemClickListener: ((T) -> Unit)? = null

    fun setOnIteClickListener(l: (T) -> Unit) {
        onItemClickListener = l
    }

    abstract override fun getItemViewType(position: Int): Int
    abstract fun onCreateViewHolder(view: View, viewType: Int): BaseViewHolder

}
