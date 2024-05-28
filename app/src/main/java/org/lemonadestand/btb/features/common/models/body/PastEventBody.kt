package org.lemonadestand.btb.features.common.models.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.http.Header
import retrofit2.http.Query


@Parcelize
data class PastEventBody(
    var limit: String,
    var page: String,
    var sort: String,
    var order_by: String,
    var resource: String,
    var completed: String,
    var archive: String,


    ) : Parcelable