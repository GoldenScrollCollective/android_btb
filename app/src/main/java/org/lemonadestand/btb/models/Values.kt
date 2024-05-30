package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Values {
	@SerializedName("description")
	var description: Description? = null

	@SerializedName("items")
	var items: List<Items>? = null
}
