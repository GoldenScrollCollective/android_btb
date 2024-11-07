package org.lemonadestand.btb.core.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address (
	val street: String,
	val street2: String,
	val city: String,
	val state: String,
	val country: String,
	val postal: String
) : Parcelable
