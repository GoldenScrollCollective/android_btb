package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String,
    @SerializedName("uniq_id") val uniqueId: String,
    @SerializedName("org_id") val orgId: String,
    @SerializedName("company_id") val companyId: String,
    override val name: String?,
    override val picture: String?,
    val email: String?,
    val phone: String?,
    val ext: String?,
    val address: Address?,
    @SerializedName("address_shipping") val shippingAddress: Address?,
    @SerializedName("last_blessed") val lastBlessed: Event?,
    override val created: String?
): BaseModel(name, picture, created) {
    val lastBlessedAt: String
        get() {
            val date = DateHelper.parse(lastBlessed?.blessingComplete, "yyyy-MM-dd HH:mm:ss") ?: return "Never"
            return DateHelper.format(date, "MMM d, yyyy") ?: "Never"
        }
}

@Parcelize
data class ContactsResponseModel(
    val status: Boolean,
    val message: String,
    val data: List<Contact>
): Parcelable