package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class LoginResponse {
	@SerializedName("status")
	var status: Boolean = false

	@SerializedName("message")
	var message: String? = null

	@SerializedName("user")
	var user: User? = null
}
