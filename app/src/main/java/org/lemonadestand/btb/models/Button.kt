package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Button {
	@SerializedName("position")
	var position: String? = null

	@SerializedName("x")
	var x: Int = 0

	@SerializedName("y")
	var y: Int = 0

	@SerializedName("append")
	var append: String? = null
}
