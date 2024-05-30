package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Form {
	@SerializedName("emailTo")
	var emailTo: String? = null

	@SerializedName("thankYouMessage")
	var thankYouMessage: ThankYouMessage? = null
}
