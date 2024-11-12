package org.lemonadestand.btb.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.features.event.models.Interest

@Parcelize
data class Resource(
    val id: String,
    val name: String,
    val picture: String? = null,
    val interests: ArrayList<Interest>? = null
) : Parcelable

enum class EventFor(val value: String) {
    team("user"),
    contact("contact"),
    company("company"),
}