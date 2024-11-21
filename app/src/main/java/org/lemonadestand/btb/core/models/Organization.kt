package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Organization(
	@SerializedName("id")
	var id: String? = null,

	@SerializedName("uniq_id")
	var uniqId: String? = null,

	@SerializedName("name")
	var name: String? = null,

	@SerializedName("picture")
	var picture: String? = null,

	@SerializedName("timezone")
	var timezone: String? = null,

	@SerializedName("widget_token")
	var widgetToken: String? = null,

	@SerializedName("widget_settings")
	var widgetSettings: WidgetSettings? = null,

	@SerializedName("qr_code")
	var qrCode: String? = null,

	@SerializedName("user_monthly_spend")
	var userMonthlySpend: String? = null,

	@SerializedName("tango_fund_threshold")
	var tangoFundThreshold: String? = null,

	@SerializedName("tango_fund_amount")
	var tangoFundAmount: String? = null,

	@SerializedName("tango_credit_card")
	var tangoCreditCard: String? = null,

	@SerializedName("created")
	var created: String? = null,

	@SerializedName("tango_account")
	var tangoAccount: TangoAccount? = null,
) : Parcelable