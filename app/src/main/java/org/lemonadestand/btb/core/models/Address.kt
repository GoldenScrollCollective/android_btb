package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
	@SerializedName("street")
	val street: String,

	@SerializedName("street2")
	val street2: String,

	@SerializedName("city")
	val city: String,

	@SerializedName("state")
	val state: String,

	@SerializedName("country")
	val country: String,

	@SerializedName("postal")
	val postal: String
) : Parcelable
