package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Interest (
    val resource: String,
    @SerializedName("field_id") val fieldId: String,
    val value: String,
    val label: String
) : Parcelable