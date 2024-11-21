package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class Handles(
	@SerializedName("linkedin")
	var linkedin: String? = null,

	@SerializedName("fb")
	var fb: String? = null,

	@SerializedName("twitter")
	var twitter: String? = null,

	@SerializedName("instagram")
	var instagram: String? = null,
) : Parcelable