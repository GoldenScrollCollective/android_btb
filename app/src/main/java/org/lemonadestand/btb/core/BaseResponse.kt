package org.lemonadestand.btb.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
open class BaseResponse(
	@Transient open val status: Boolean,
	@Transient open val message: String? = null
) : Parcelable