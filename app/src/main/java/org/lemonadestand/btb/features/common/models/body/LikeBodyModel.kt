package org.lemonadestand.btb.features.common.models.body

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LikeBodyModel (
    var metaName : String,
    var metaValue : String,
    var byUserId : String,
    var uniqueId : String,
)  :Parcelable

@Parcelize
data class LikeRequestBodyModel (

    var meta_name : String,
    var meta_value : String,
    var by_user_id : String,

)  :Parcelable