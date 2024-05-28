package org.lemonadestand.btb.features.common.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CommonResponseModel(
    var status: String,
    var message: String

) : Parcelable