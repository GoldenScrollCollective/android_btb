package org.lemonadestand.btb.features.event.adapter


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chantsune.swipetoaction.views.SimpleSwipeLayout
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.show
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.event.models.EventModel


class EventSubAdapter(
    private val list: ArrayList<EventModel>,
    var context: Context,
    private var superPosition: Int
) :
    RecyclerView.Adapter<EventSubAdapter.ViewHolder>() {
    private var onItemClick: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.row_event_sub, parent, false)
        return ViewHolder(listItem)
    }

    fun setItemClick(onItemClickListener: OnItemClickListener) {
        this.onItemClick = onItemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]



        holder.tvTitle.text = data.resource.name
        holder.tvDesc.text = data.title

        if(data.blessing_sent != null && data.blessing_complete!=null) {
            holder.tvDesc.text = data.blessing_sent
        }

        holder.tvInterest.hide()
        if(data.resource.interests != null)
        {

            holder.tvInterest.show()


            val result = buildString {
                for (number in data.resource.interests) {
                    append(number.label)
                    append(" ➜ ")
                    append(number.value)
                    append("\n")
                }
            }
            holder.tvInterest.text = result


        }



        if (data.resource.picture != null) {
            // show image
            Glide.with(context).load(data.resource.picture).into(holder.ivImage)
        } else {

            Log.e("url=>", data.resource.name.trim().lowercase().getImageUrlFromName())
            Glide.with(context).load(data.resource.name.trim().lowercase().getImageUrlFromName())
                .into(
                    holder.ivImage
                )
        }



        holder.swipeLayout.setOnSwipeItemClickListener { swipeItem ->

            when (swipeItem.position) {
                0 -> {
                    onItemClick!!.onItemClicked(
                        data,
                        position,
                        ClickType.DELETE_EVENT,
                        superIndex = superPosition
                    )

                }

                1 -> {
                    Toast.makeText(holder.itemView.context, "Settings", Toast.LENGTH_SHORT)
                        .show()
                }

                2 -> {
                    val pos = holder.absoluteAdapterPosition

                    notifyItemRemoved(pos)
                    Toast.makeText(holder.itemView.context, "Trash", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

        holder.itemView.setOnClickListener {
            onItemClick!!.onItemClicked(
                data,
                position,
                ClickType.EDIT_EVENT,
                superIndex = superPosition
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView

         var tvDesc: TextView
        var ivImage: ImageView
        var swipeLayout: SimpleSwipeLayout
        var tvInterest: TextView


        init {
            tvTitle = itemView.findViewById(R.id.tv_title)
            tvDesc = itemView.findViewById(R.id.tv_desc)
            ivImage = itemView.findViewById(R.id.iv_image)
            swipeLayout = itemView.findViewById(R.id.swipe_layout)
            tvInterest = itemView.findViewById(R.id.tv_interest)
        }
    }
}