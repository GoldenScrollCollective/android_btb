package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class DateModel(
	@SerializedName("date")
	var date: String? = null,

	@SerializedName("timezone_type")
	var timezoneType: Int = 0,

	@SerializedName("timezone")
	var timezone: String? = null,
) : Parcelable
