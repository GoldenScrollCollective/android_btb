package org.lemonadestand.btb.core.models

import org.lemonadestand.btb.features.post.models.Post

data class CommentsByDate(
	var commentList: ArrayList<Post> = ArrayList(),
	var date: String? = null
)