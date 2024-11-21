package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.constants.getImageUrlFromName
import java.util.Locale
import kotlin.math.min

@Parcelize
data class User(
	@SerializedName("id")
	var id: String = "",

	@SerializedName("uniq_id")
	var uniqueId: String = "",

	@SerializedName("org_id")
	var orgId: String? = null,

	@SerializedName("username")
	var username: String? = null,

	@SerializedName("status")
	var status: String? = null,

	@SerializedName("status_message")
	var statusMessage: String? = null,

	@SerializedName("active")
	var active: Boolean = false,

	@SerializedName("last_active")
	var lastActive: DateModel? = null,

	@SerializedName("created_at")
	var createdAt: DateModel? = null,

	@SerializedName("updated_at")
	var updatedAt: DateModel? = null,

	@SerializedName("deleted_at")
	var deletedAt: String? = null,

	@SerializedName("name")
	var name: String = "",

	@SerializedName("picture")
	var picture: String? = null,

	@SerializedName("phone")
	var phone: String? = null,

	@SerializedName("dob")
	var dob: String? = null,

	@SerializedName("address")
	var address: Address? = null,

	@SerializedName("address_shipping")
	var addressShipping: Address? = null,

	@SerializedName("title")
	var title: String? = null,

	@SerializedName("handles")
	var handles: Handles? = null,

	@SerializedName("public")
	var public: String? = null,

	@SerializedName("give")
	var give: String = "",

	@SerializedName("spend")
	var spend: String = "",

	@SerializedName("user_hash")
	var userHash: String? = null,

	@SerializedName("groups")
	var groups: List<String> = listOf(),

	@SerializedName("permissions")
	var permissions: List<String> = listOf(),

	@SerializedName("organization")
	var organization: Organization? = null,

	@SerializedName("token")
	var token: Token? = null,
) : Parcelable {


	val shortName: String
		get() {
			val splitedNames =
				name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			val names = ArrayList<String>()
			for (i in 0 until min(splitedNames.size.toDouble(), 2.0).toInt()) {
				names.add(splitedNames[i].substring(0, 1).uppercase(Locale.getDefault()))
			}
			return java.lang.String.join("", names)
		}

	val pictureUrl: String?
		get() {
			if (picture != null) return picture
			if (name == null) return ""
			return name.trim().lowercase().getImageUrlFromName()
		}
}
