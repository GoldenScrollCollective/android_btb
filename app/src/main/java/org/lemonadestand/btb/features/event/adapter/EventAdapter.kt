package org.lemonadestand.btb.features.event.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.event.models.EventModel
import org.lemonadestand.btb.features.event.models.EventModelDate

class EventAdapter(private var list: ArrayList<EventModelDate>, var context: Context): RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    private var onItemClick: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.row_event, parent, false)
        return ViewHolder(listItem)
    }

    fun setOnItemClick(onItemClickListener: OnItemClickListener) {
        this.onItemClick = onItemClickListener
    }

    @SuppressWarnings("InvalidSetHasFixedSize")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvDate.text = getDate(data.date!!)

        val tempPostList: ArrayList<EventModel> = ArrayList()

        for (i in 0 until data.eventList.size) {
            if (data.eventList[i].blessing_complete != null)
            {
                if (getDate(data.eventList[i].blessing_complete!!) == getDate(data.date!!))
                {
                    tempPostList.add(data.eventList[i])
                }

            }
            else  if (getDate(data.eventList[i].start) == getDate(data.date!!)) {
                tempPostList.add(data.eventList[i])
            }
        }
        val adapter = EventSubAdapter(tempPostList, context, position)
        onItemClick?.let { adapter.setItemClick(it) }
        holder.recyclerView.setHasFixedSize(true)
        holder.recyclerView.layoutManager = LinearLayoutManager(context)
        holder.recyclerView.adapter = adapter
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDate: TextView
        var recyclerView: RecyclerView

        init {
            tvDate = itemView.findViewById(R.id.tv_date)
            recyclerView = itemView.findViewById(R.id.rv_public_sub)
        }
    }

    fun updateData(list: ArrayList<EventModelDate>) {
        this.list = list
        notifyDataSetChanged()
    }
}