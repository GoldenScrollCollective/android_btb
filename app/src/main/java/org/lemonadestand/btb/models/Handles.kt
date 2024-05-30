package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Handles {
	@SerializedName("linkedin")
	var linkedin: String? = null

	@SerializedName("fb")
	var fb: String? = null

	@SerializedName("twitter")
	var twitter: String? = null

	@SerializedName("instagram")
	var instagram: String? = null
}
