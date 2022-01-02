package app.model

import androidx.recyclerview.widget.DiffUtil

typealias BaseItems = List<BaseItem>


interface BaseItem {
    val uniqueId: String

    override operator fun equals(other: Any?): Boolean
}

fun <T : BaseItem> getBaseItemDiffUtil() = object : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.uniqueId == newItem.uniqueId
}

//tools
val emptyItem = object : BaseItem {
    override val uniqueId: String
        get() = hashCode().toString()

    override fun equals(other: Any?): Boolean {
        return (other as? BaseItem)?.uniqueId == uniqueId
    }
}

fun emptyItems(size: Int = 20) = arrayListOf<BaseItem>().apply {
    for (i in 0 until size) add(emptyItem)
}

