package org.lemonadestand.btb.features.event.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class EventModel(
    val id: String,


    @SerializedName("uniq_id") val uniqueId: String,

    val org_id: String,

    val user_id: String? = null,


    val by_user_id: String,

    val resource: BlessingByUserID,
    val title: String,
    val description: String,
    val repeating: String,
    val frequency: String,


    val all_day: String,

    val start: Date?,
    val end: Date?,
    val reminder: Date? = null,

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
) : Parcelable {
    enum class Frequency(val value: String) {
        once("once"),
        yearly("yearly");
    }

    val startedAt: String?
        get() = DateHelper.format(start, "yyyy-MM-dd HH:mm:ss")
}