package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.BasePictureModel
import org.lemonadestand.btb.core.BaseResponse
import java.util.Date

@Parcelize
data class Member(
	override val id: String,
	@SerializedName("uniq_id") val uniqueId: String,
	override val name: String,
	override val picture: String,
	@SerializedName("last_blessed") val lastBlessed: BlessingEvent? = null,
	@SerializedName("last_appreciated") val lastAppreciated: Date?,
	override val created: Date?
) : BasePictureModel(id, name, picture, created) {
	@Parcelize
	data class BlessingEvent(
		@SerializedName("blessing_complete") val blessingComplete: Date?,
	) : Parcelable {
		val blessingCompletedAt: String
			get() = DateHelper.format(blessingComplete, "MMM d, yyyy") ?: "Never"
	}

	val lastBlessedAt: String
		get() = lastBlessed?.blessingCompletedAt ?: "Never"

	val lastAppreciatedAt: String
		get() {
			return DateHelper.format(lastAppreciated, "MMM d, yyyy") ?: "Never"
		}
}

@Parcelize
data class MembersResponse(
	override val status: Boolean,
	override val message: String,
	val data: ArrayList<Member> = arrayListOf()
) : BaseResponse(status, message)