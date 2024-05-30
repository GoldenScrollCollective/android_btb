package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Address {
	@SerializedName("street")
	var street: String? = null

	@SerializedName("street2")
	var street2: String? = null

	@SerializedName("city")
	var city: String? = null

	@SerializedName("state")
	var state: String? = null

	@SerializedName("country")
	var country: String? = null

	@SerializedName("postal")
	var postal: String? = null
}
