package org.lemonadestand.btb.core.response

import com.google.gson.annotations.SerializedName
import org.lemonadestand.btb.core.models.User

class LoginResponse {
	@SerializedName("status")
	var status: Boolean = false

	@SerializedName("message")
	var message: String? = null

	@SerializedName("user")
	var user: User? = null
}
