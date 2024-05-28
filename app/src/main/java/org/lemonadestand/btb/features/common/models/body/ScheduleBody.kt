package org.lemonadestand.btb.features.common.models.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ScheduleBody (
    var limit : String,
    var page : String,
    var sort : String,
    var order_by : String,
    var resource : String,
    var completed : String,
)  :Parcelable