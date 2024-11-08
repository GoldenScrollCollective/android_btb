package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Company(
    override val id: String,
    @SerializedName("uniq_id") val uniqueId: String,
    @SerializedName("org_id") val orgId: String,
    override val name: String,
    override val picture: String?,
    val email: String,
    @SerializedName("last_blessed") val lastBlessed: BlessingEvent?,
    override val created: Date?,
): BasePictureModel(id, name, picture, created) {
    @Parcelize
    data class BlessingEvent(
        @SerializedName("blessing_complete") val blessingComplete: Date?,
    ): Parcelable {
        val blessingCompletedAt: String
            get() = DateHelper.format(blessingComplete, "MMM d, yyyy") ?: "Never"
    }

    val lastBlessedAt: String
        get() = lastBlessed?.blessingCompletedAt ?: "Never"
}

@Parcelize
data class CompanyListResponseModel(
    val status: Boolean,
    val message: String,
    val data: List<Company>
): Parcelable