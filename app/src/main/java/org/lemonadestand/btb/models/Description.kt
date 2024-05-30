package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Description {
	@SerializedName("type")
	var type: String? = null

	@SerializedName("content")
	var content: List<Content>? = null
}
