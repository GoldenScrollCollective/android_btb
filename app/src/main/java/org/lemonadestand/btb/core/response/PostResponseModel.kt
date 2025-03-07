package org.lemonadestand.btb.core.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.models.Post

@Parcelize
data class PostResponseModel(
	val status: Boolean,
	val message: String,
	val data: ArrayList<Post>? = arrayListOf()
) : Parcelable