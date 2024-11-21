package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Button(
	@SerializedName("position")
	var position: String? = null,

	@SerializedName("x")
	var x: Int = 0,

	@SerializedName("y")
	var y: Int = 0,

	@SerializedName("append")
	var append: String? = null,
) : Parcelable
