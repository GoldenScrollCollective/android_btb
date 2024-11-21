package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Form(
	@SerializedName("emailTo")
	var emailTo: String? = null,

	@SerializedName("thankYouMessage")
	var thankYouMessage: ThankYouMessage? = null,
) : Parcelable
