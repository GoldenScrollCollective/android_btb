package org.lemonadestand.btb.features.event.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlessingByUserID(
    val id: String,
    val name: String,
    val picture: String? = null,
    val interests: ArrayList<Interest>? = null
) : Parcelable


@Parcelize
data class Interest (
    val resource: String,
    val field_id: String,
    val value: String,
    val label: String
) : Parcelable