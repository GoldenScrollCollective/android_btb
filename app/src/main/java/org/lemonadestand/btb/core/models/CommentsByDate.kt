package org.lemonadestand.btb.core.models

data class CommentsByDate(
	var commentList: ArrayList<Post> = ArrayList(),
	var date: String? = null
)