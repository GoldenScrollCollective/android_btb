package org.lemonadestand.btb.features.event.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EventModel(
    val id: String,


    val uniq_id: String,

    val org_id: String,

    val user_id: String? = null,


    val by_user_id: String,

    val resource: BlessingByUserID,
    val title: String,
    val description: String,
    val repeating: String,
    val frequency: String,


    val all_day: String,

    val start: String,
    val end: String,
    val reminder: String? = null,

    val interactive: String,


    val blessing_complete: String ? = null,


    val blessing_sent: String ? = null,


    val blessing_value: String,

    val blessing_notes: String,
    val parent : BlessingByUserID? = null,
    var notify : ArrayList<String>  = ArrayList(),


    val blessing_by_user_id: BlessingByUserID? = null,

    val transaction_id: String? = null,

    val created: String
) : Parcelable


