package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class BlessEvent(
    val id: String,
    @SerializedName("blessing_complete") val blessingComplete: String
): Parcelable

@Parcelize
data class Member(
    val id: String,
    @SerializedName("uniq_id") val uniqueId: String,
    val name: String,
    val picture: String,
    @SerializedName("last_blessed") val lastBlessed: BlessEvent?,
    @SerializedName("last_appreciated") val lastAppreciated: String?
): Parcelable {
    val lastBlessedAt: String
        get() {
            val date = DateHelper.parse(lastBlessed?.blessingComplete, "yyyy-MM-dd HH:mm:ss") ?: return "Never"
            return DateHelper.format(date, "MMM d, yyyy") ?: "Never"
        }
    val lastAppreciatedAt: String
        get() {
            val date = DateHelper.parse(lastAppreciated, "yyyy-MM-dd HH:mm:ss") ?: return "Never"
            return DateHelper.format(date, "MMM d, yyyy") ?: "Never"
        }
}

@Parcelize
data class MemberListResponseModel(
    val status: Boolean,
    val message: String,
    val data: List<Member>
): Parcelable