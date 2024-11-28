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
	@SerializedName("uniq_id") val uniqueId: String,
	val type: String? = null,
	val parentID: String? = null,
	val byUserID: String,
	val resource: String,
	val title: String? = null,
	val body: DatumBody,
	val visibility: String,
	val media: String?,
	override val created: Date?,
	val modified: String,
	val depth: String,
	val meta: DatumMeta,
	val replies: ArrayList<Post>,
	val html: String,
	@SerializedName("by_user") val byUser: User,
	val user: User,
	val users: ArrayList<User> = arrayListOf(),
	val organization: Resource? = null
) : BaseModel(id, created) {
	enum class Visibility(val value: String) {
		PUBLIC("public"),
		RESOURCE("resource"),
		MINE("mine"),
		ORGANIZATION("organization")
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
	val like: ArrayList<Bonus>? = null,
) : Parcelable

@Parcelize
data class Bonus(
	val by_user: User,
	val value: String
) : Parcelable