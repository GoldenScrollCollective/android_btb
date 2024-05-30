package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class CreatedAt {
	@SerializedName("date")
	var date: String? = null

	@SerializedName("timezone_type")
	var timezoneType: Int = 0

	@SerializedName("timezone")
	var timezone: String? = null
}
