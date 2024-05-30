package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class WidgetSettings {
	@SerializedName("button")
	var button: Button? = null

	@SerializedName("values")
	var values: Values? = null

	@SerializedName("form")
	var form: Form? = null
}
