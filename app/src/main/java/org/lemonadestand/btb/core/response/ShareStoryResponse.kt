package org.lemonadestand.btb.core.response

import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.core.models.Post

@Parcelize
data class ShareStoryResponse(
	override val status: Boolean,
	override val message: String,
	val data: Post? = null
) : BaseResponse(status, message)