package org.lemonadestand.btb.core.models

import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Event(
    override val id: String,
    @SerializedName("uniq_id") val uniqueId: String?,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("by_user_id") val byUserId: String?,
    val title: String = "",
    val description: String?,
    val repeating: String = "",
    val frequency: String = "",
    val start: Date?,
    val end: Date?,
    val reminder: Date?,
    var notify: String?,
    val interactive: String = "1",
    val parent: Resource?,
    val resource: Resource?,
    @SerializedName("blessing_complete") val blessingComplete: Date?,
    @SerializedName("blessing_sent") val blessingSent: String?,
    @SerializedName("blessing_value") val blessingValue: String?,
    @SerializedName("blessing_notes") val blessingNotes: String?,
    @SerializedName("blessing_by_user_id") val blessingByUserId: String?,
    override var created: Date? = Date()
): BaseModel(id, created) {
    val blessingCompletedAt: String
        get() = DateHelper.format(blessingComplete, "MMM d, yyyy") ?: "Never"
}