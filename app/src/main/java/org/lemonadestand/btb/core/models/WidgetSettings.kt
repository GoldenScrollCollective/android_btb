package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class WidgetSettings(
	@SerializedName("button")
	var button: Button? = null,

	@SerializedName("values")
	var values: Values? = null,

	@SerializedName("form")
	var form: Form? = null,
) : Parcelable
