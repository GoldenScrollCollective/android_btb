package org.lemonadestand.btb.features.post.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class PostResponseModel (
    val status: Boolean,
    val message: String,
    val data: List<PostModel>
) : Parcelable

@Parcelize
data class PostModel (
    val id: String,
    val uniq_id: String,
    val type: String? = null,
    val parentID: String? = null,
    val byUserID: String,
    val resource: String,
    val title: String? = null,
    val body: DatumBody,
    val visibility: String,
    val created: String,
    val modified: String,
    val depth: String,
    val meta: DatumMeta,
    val replies: List<PostModel>,
    val html: String,
    val by_user: User,
    val user: User,
    val users: ArrayList<User>,
): Parcelable {
    fun modifiedAt(): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            return format.parse(modified)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

@Parcelize
data class DatumBody (
    val type: String,
    val content: List<PurpleContent>
): Parcelable


@Parcelize

data class PurpleContent (
    val type: String,
    val content: List<FluffyContent>
): Parcelable

@Parcelize
data class FluffyContent (
    val type: String,
    val text: String,
    val marks: List<Mark>? = null
): Parcelable

@Parcelize
data class Mark (
    val type: String
): Parcelable



@Parcelize
data class User (
    val id: String ? = null,
    val name: String ? = null,
    val picture: String? = null
): Parcelable



@Parcelize
data class DatumMeta (
    val bonus: List<Bonus>? = null,
    val debit: List<Bonus>? = null,
    val like: ArrayList<Bonus>  = ArrayList()
): Parcelable

@Parcelize
data class Bonus (
    val byUser: User,
    val value: String
): Parcelable
/*
@Parcelize
data class Reply (
    val id: String,
    val uniqID: String,
    val type: String,
    val parentID: String,
    val byUserID: String,
    val resource: String,
    val title: String? = null,
    val body: ReplyBody,
    val visibility: String,
    val created: String,
    val modified: String,
    val depth: String,
    val meta: ReplyMeta,
    val replies: List<Reply>,
    val html: String,
    val by_user: User,
    val user: User
): Parcelable

@Parcelize
data class ReplyBody (
    val type: String,
    val content: List<TentacledContent>
): Parcelable

@Parcelize
data class TentacledContent (
    val type: String,
    val content: List<StickyContent>
): Parcelable

@Parcelize
data class StickyContent (
    val type: String,
    val attrs: Attrs? = null,
    val text: String? = null
): Parcelable

@Parcelize
data class Attrs (
    val name: String
): Parcelable

@Parcelize
data class ReplyMeta (
    val debit: List<Bonus>,
    val bonus: List<Bonus>,
    val like: List<Bonus> ? = null
): Parcelable*/

