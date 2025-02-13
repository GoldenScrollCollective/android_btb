package org.lemonadestand.btb.features.common.models.body

import org.lemonadestand.btb.core.models.DatumMeta
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.models.Resource
import org.lemonadestand.btb.core.models.User


data class ShareStoryUser(
	var id: String,
	var name: String,
	var picture: String? = null,
)

data class ShareStoryBody(
	val uniq_id: String,
	val resource: String,
	val title: String? = null,
	val html: String,
	val media: String? = null,
	val parent_id: String,
	val by_user_id: String,
	val by_user: User? = null,
	val user: ShareStoryUser,
	val visibility: String? = null,
	val anonymous: String = "0",
	val type: String? = null,
	val bonus: String? = null,
	val debit: String? = null,
	val replies: ArrayList<Post>? = null,
	val meta: DatumMeta? = null,
	val organization: Resource? = null,
	val users: ArrayList<User>? = null,
)
