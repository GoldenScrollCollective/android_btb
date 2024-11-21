package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Items(
	@SerializedName("title")
	var title: String? = null,

	@SerializedName("description")
	var description: String? = null,

	@SerializedName("icon")
	var icon: String? = null,
) : Parcelable
