package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Description(
	@SerializedName("type")
	var type: String? = null,

	@SerializedName("content")
	var content: List<Content>? = null,
) : Parcelable
