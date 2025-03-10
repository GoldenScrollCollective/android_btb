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
	val uniq_id: String? = null,
	val resource: String? = null,
	val title: String? = null,
	val html: String? = null,
	val media: String? = null,
	val parent_id: String? = null,
	val by_user_id: String,
	val by_user: User? = null,
	val user: ShareStoryUser? = null,
	val visibility: String? = null,
	val anonymous: String? = null,
	val type: String? = null,
	val bonus: String? = null,
	val debit: String? = null,
	val replies: ArrayList<Post>? = null,
	val meta: DatumMeta? = null,
	val organization: Resource? = null,
	val users: ArrayList<String>? = null,
)
