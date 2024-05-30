package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class Token {
	@SerializedName("iss")
	var iss: String? = null

	@SerializedName("user_id")
	var userId: String? = null

	@SerializedName("name")
	var name: String? = null

	@SerializedName("email")
	var email: String? = null

	@SerializedName("sub")
	var sub: String? = null

	@SerializedName("iat")
	var iat: Int = 0

	@SerializedName("exp")
	var exp: Int = 0

	@SerializedName("raw_token")
	var rawToken: String? = null

	@SerializedName("refresh_token")
	var refreshToken: String? = null
}
