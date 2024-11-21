package org.lemonadestand.btb.core.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.models.Event

@Parcelize
data class EventResponseModel(
	val status: Boolean,
	val message: String,
	val data: List<Event>
) : Parcelable