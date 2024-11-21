package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Values(
	@SerializedName("description")
	var description: Description? = null,

	@SerializedName("items")
	var items: List<Items>? = null,
) : Parcelable
