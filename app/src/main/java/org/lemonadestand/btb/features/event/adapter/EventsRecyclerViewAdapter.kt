package org.lemonadestand.btb.features.event.adapter


import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.chantsune.swipetoaction.views.SimpleSwipeLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.show

class EventsRecyclerViewAdapter : BaseRecyclerViewAdapter<Event>(R.layout.layout_events_item, fullHeight = true) {
	var onDelete: ((value: Event) -> Unit)? = null

	override fun bindView(holder: ViewHolder, item: Event, position: Int) {
		super.bindView(holder, item, position)

		with(holder.itemView) {
			val avatarView = findViewById<ImageView>(R.id.avatarView)
			Glide.with(context).load(item.resource?.pictureUrl).into(avatarView)

			val nameView = findViewById<TextView>(R.id.nameView)
			nameView.text = item.resource?.name

			val descriptionView = findViewById<TextView>(R.id.descriptionView)
			descriptionView.text = item.title
			if (item.blessingSent != null && item.blessingComplete != null) {
				descriptionView.text = item.blessingSent
			}

			val interestView = findViewById<TextView>(R.id.interestView)
			interestView.hide()
			if (item.resource?.interests != null) {
				interestView.show()
				interestView.text = buildString {
					for (number in item.resource.interests) {
						append(number.label)
						append(" ➜ ")
						append(number.value)
						append("\n")
					}
				}
			}

			val swipeLayout = findViewById<SimpleSwipeLayout>(R.id.swipeLayout)
			swipeLayout.setOnSwipeItemClickListener { swipeItem ->

				when (swipeItem.position) {
					0 -> {
						values?.removeAt(position)
						notifyItemRemoved(position)
						onDelete?.invoke(item)
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
		}
	}
}