package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.BaseModel
import java.util.Date

data class PostsByDate(
	var posts: ArrayList<Post> = ArrayList(),
	var date: Date? = null
)

@Parcelize
data class Post(
	override val id: String,
	@SerializedName("uniq_id") var uniqueId: String,
	var type: String? = null,
	@SerializedName("parent_id") var parentId: String? = null,
	@SerializedName("by_user_id") var byUserId: String? = null,
	var resource: String? = null,
	var title: String? = null,
	var body: DatumBody,
	var visibility: String,
	var media: String?,
	var bonus: String?,
	var debit: String?,
	override val created: Date?,
	var modified: String,
	var depth: String,
	var meta: DatumMeta,
	var replies: ArrayList<Post>,
	var html: String,
	@SerializedName("by_user") val byUser: User? = null,
	var user: User,
	var users: ArrayList<User> = arrayListOf(),
	var organization: Resource? = null,
	var anonymous: String? = null
) : BaseModel(id, created) {
	enum class Visibility(val value: String) {
		PUBLIC("public"),
		RESOURCE("resource"),
		MINE("mine"),
		ORGANIZATION("organization")
	}

	enum class Debit(val value: String) {
		GIVE("give"),
		SPEND("spend"),
	}
}

@Parcelize
data class DatumBody(
	val type: String,
	val content: List<PurpleContent>
) : Parcelable


@Parcelize
data class PurpleContent(
	val type: String,
	val content: List<FluffyContent>
) : Parcelable

@Parcelize
data class FluffyContent(
	val type: String,
	val text: String,
	val marks: List<Mark>? = null
) : Parcelable

@Parcelize
data class Mark(
	val type: String
) : Parcelable


@Parcelize
data class DatumMeta(
	val bonus: List<Bonus>? = null,
	val debit: List<Bonus>? = null,
	val like: ArrayList<Bonus> = arrayListOf(),
) : Parcelable

@Parcelize
data class Bonus(
	val by_user: User,
	val value: String
) : Parcelable