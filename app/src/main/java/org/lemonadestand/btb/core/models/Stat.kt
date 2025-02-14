package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stat(
	val stories: Int,
	val recognized: Int,
	val appreciation: Int,
	@SerializedName("dollar_impact") val dollarImpact: Float,
	@SerializedName("lives_blessed") val livesBlessed: Int
) : Parcelable