package org.lemonadestand.btb.components.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.extenstions.setOnSingleClickListener

open class BaseRecyclerViewAdapter<T>(@LayoutRes val layoutResId: Int, val fullHeight: Boolean = false): RecyclerView.Adapter<BaseRecyclerViewAdapter<T>.ViewHolder>() {
    var values: List<T>? = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClick: ((value: T) -> Unit)? = null
    protected var recyclerView: RecyclerView? = null

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {	}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutResId, parent, false))
    }

    override fun getItemCount(): Int = values?.size ?: 0

    open fun getItem(position: Int): T = values!![position]

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: T = values!![position]
        holder.itemView.setOnSingleClickListener {
            onItemClick?.invoke(item)
        }

        bindView(holder, item, position)

        if (fullHeight) {
            val params = recyclerView?.layoutParams ?: return
            params.height = values!!.size * holder.itemView.layoutParams.height
            recyclerView?.layoutParams = params
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView

        recyclerView.scrollToPosition(itemCount - 1)
        recyclerView.scrollToPosition(0)
    }

    open protected fun bindView(holder: ViewHolder, item: T, position: Int) {}
}