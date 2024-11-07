package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Company(
    val id: String,
    @SerializedName("uniq_id") val uniqueId: String,
    @SerializedName("org_id") val orgId: String,
    val name: String,
    val picture: String?,
    val email: String,
    @SerializedName("last_blessed") val lastBlessed: Event?,
    @SerializedName("created") val createdAt: String,
): Parcelable {
    val lastBlessedAt: String
        get() {
            val date = DateHelper.parse(lastBlessed?.blessingComplete, "yyyy-MM-dd HH:mm:ss") ?: return "Never"
            return DateHelper.format(date, "MMM d, yyyy") ?: "Never"
        }
}

@Parcelize
data class CompanyListResponseModel(
    val status: Boolean,
    val message: String,
    val data: List<Company>
): Parcelable