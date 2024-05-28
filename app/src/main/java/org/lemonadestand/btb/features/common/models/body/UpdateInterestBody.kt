package org.lemonadestand.btb.features.common.models.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UpdateInterestBody (
    val fields: ArrayList<Field>
):Parcelable



@Parcelize
data class Field (
    val value: String
):Parcelable
