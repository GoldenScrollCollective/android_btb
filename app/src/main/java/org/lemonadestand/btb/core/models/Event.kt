package org.lemonadestand.btb.core.models

import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.BaseModel
import java.util.Date

@Parcelize
data class Event(
	override val id: String,
	@SerializedName("uniq_id") val uniqueId: String = "",
	@SerializedName("user_id") val userId: String?,
	@SerializedName("by_user_id") val byUserId: String?,
	val title: String = "",
	val description: String = "",
	val repeating: String = "",
	val frequency: String = "",
	val start: Date?,
	val end: Date?,
	val reminder: Date?,
	var notify: ArrayList<String> = arrayListOf(),
	val interactive: String = "1",
	val parent: Resource?,
	val resource: Resource?,
	@SerializedName("blessing_complete") val blessingComplete: Date?,
	@SerializedName("blessing_sent") val blessingSent: String?,
	@SerializedName("blessing_value") val blessingValue: String?,
	@SerializedName("blessing_notes") val blessingNotes: String?,
	@SerializedName("blessing_by_user_id") val blessingByUser: Resource?,
	override var created: Date? = Date()
) : BaseModel(id, created) {
	enum class Frequency(val value: String) {
		once("once"),
		yearly("yearly");
	}

	val blessingCompletedAt: String
		get() = DateHelper.format(blessingComplete, "MMM d, yyyy") ?: "Never"

	val blessingCompletedDay: String?
		get() = DateHelper.format(blessingComplete, "EEE, MMM dd, yyyy")

	val startedAt: String?
		get() = DateHelper.format(start, "yyyy-MM-dd HH:mm:ss")

	val startedDay: String?
		get() = DateHelper.format(start, "EEE, MMM dd, yyyy")
}

data class EventsPerDate(
	val events: ArrayList<Event> = arrayListOf(),
	val date: String? = null
) {
	companion object {
		fun groupEvents(events: ArrayList<Event>): ArrayList<EventsPerDate> {
			val eventsPerDates = arrayListOf<EventsPerDate>()
			val dateList: ArrayList<String> = ArrayList()

			for (i in 0 until events.size) {
				val event = events[i]
				val day = event.blessingCompletedDay ?: event.startedDay ?: continue
				if (!dateList.contains(day)) {
					dateList.add(day)
					eventsPerDates.add(
						EventsPerDate(
							date = event.blessingCompletedDay ?: event.startedDay,
							events = ArrayList(events.filter { x -> x.blessingCompletedDay == day || x.startedDay == day })
						)
					)
				}
			}

			return eventsPerDates
		}
	}
}