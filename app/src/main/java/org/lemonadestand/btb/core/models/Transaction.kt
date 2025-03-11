package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.core.BaseModel
import java.util.Date

@Parcelize
data class Transaction(
	override val id: String,
	@SerializedName("org_id") val orgId: String?,
	@SerializedName("by_user_id") var byUserId: String? = null,
	@SerializedName("post_id") var postId: String? = null,
	@SerializedName("transaction_id") var transactionId: String? = null,
	var type: String? = null,
	@SerializedName("card_id") var cardId: String? = null,
	var amount: String? = null,
	var state: String? = null,
	@SerializedName("transaction_date") var transactionDate: Date? = null,
	val settled: Date?,
	val settled_date: Date?,
	val completed: Date?,
	val isComplete: Date?,
	val description: String?,
	val merchant: Merchant,
	override val created: Date?,
) : BaseModel(id, created)


@Parcelize
data class Merchant(
	val id: String,
	val name: String,
	val city: String,
	val state: String,
	@SerializedName("postal_code") val postalCode: String,
	val country: String,
	val latitude: String,
	val longitude: String,
) : Parcelable