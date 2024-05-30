package org.lemonadestand.btb.models

import com.google.gson.annotations.SerializedName

class TangoAccount {
	@SerializedName("accountIdentifier")
	var accountIdentifier: String? = null

	@SerializedName("accountNumber")
	var accountNumber: String? = null

	@SerializedName("displayName")
	var displayName: String? = null

	@SerializedName("currencyCode")
	var currencyCode: String? = null

	@SerializedName("currentBalance")
	var currentBalance: Int = 0

	@SerializedName("createdAt")
	var createdAt: String? = null

	@SerializedName("status")
	var status: String? = null

	@SerializedName("contactEmail")
	var contactEmail: String? = null
}
