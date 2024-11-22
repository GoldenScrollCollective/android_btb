package org.lemonadestand.btb.features.event.adapter

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.core.models.EventsPerDate

class EventsByDateRecyclerViewAdapter : BaseRecyclerViewAdapter<EventsPerDate>(R.layout.layout_events_by_date_item) {
	var onSelect: ((value: Event) -> Unit)? = null
	var onDelete: ((value: Event) -> Unit)? = null

	override fun bindView(holder: ViewHolder, item: EventsPerDate, position: Int) {
		super.bindView(holder, item, position)

		with(holder.itemView) {
			val dateView = findViewById<TextView>(R.id.dateView)
			dateView.text = getDate(item.date)

			val eventsRecyclerView = findViewById<RecyclerView>(R.id.eventsRecyclerView)
			eventsRecyclerView.setHasFixedSize(true)
			eventsRecyclerView.adapter = EventsRecyclerViewAdapter().apply {
				onItemClick = { onSelect?.invoke(it) }
				onDelete = {
					this@EventsByDateRecyclerViewAdapter.onDelete?.invoke(it)
				}
			}
			(eventsRecyclerView.adapter as EventsRecyclerViewAdapter).values = item.events
		}
	}
}