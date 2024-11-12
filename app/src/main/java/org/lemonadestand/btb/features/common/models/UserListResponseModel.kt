package org.lemonadestand.btb.features.common.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.models.Address


@Parcelize
data class UserListResponseModel (
    val status: Boolean,
    val message: String,
    val data: List<UserListModel>
) : Parcelable


@Parcelize
data class UserListModel (
    val id: String = "",
    @SerializedName("uniq_id") val uniqueId: String = "",
    val org_id: String = "",
    val username: String = "",
    val active: String = "",
    val lastActive: String? = null,
    val createdAt: String? = null,
    val updatedAt: String ? = null,
    val deletedAt: String? = null,
    var name: String = "",
    val lastBlessed: String? = null,

    val picture: String? = null,
    val phone: String = "",
    val dob: String? = null,
    val address: Address? = null,
    val addressShipping: Address? = null,
    val title: String? = null,
    val handles: Handles? = null,
    val public: String? =null,
    val give: String = "",
    val spend: String = "",
    val visibleProfileFields: List<String>? = null,
    val email: String? = null,
    val qrCode: String? = null,
    var isSelected : Boolean = false
)  :Parcelable

@Parcelize
data class Handles (
    val linkedin: String,
    val twitter: String,
    val fb: String,
    val instagram: String
) : Parcelable

