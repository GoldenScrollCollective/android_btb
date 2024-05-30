package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Items {
	@SerializedName("title")
	var title: String? = null

	@SerializedName("description")
	var description: String? = null

	@SerializedName("icon")
	var icon: String? = null
}
