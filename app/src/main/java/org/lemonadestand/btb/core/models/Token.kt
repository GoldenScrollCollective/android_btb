package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class Token(
	@SerializedName("user_id")
	var userId: String? = null,

	@SerializedName("name")
	var name: String? = null,

	@SerializedName("email")
	var email: String? = null,

	@SerializedName("sub")
	var sub: String? = null,

	@SerializedName("iss")
	var iss: String? = null,

	@SerializedName("iat")
	var iat: Int = 0,

	@SerializedName("exp")
	var exp: Int = 0,

	@SerializedName("raw_token")
	var rawToken: String = "",

	@SerializedName("refresh_token")
	var refreshToken: String? = null
) : Parcelable