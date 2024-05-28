package org.lemonadestand.btb.features.event.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventResponseModel (
    val status: Boolean,
    val message: String,
    val data: List<EventModel>
) : Parcelable