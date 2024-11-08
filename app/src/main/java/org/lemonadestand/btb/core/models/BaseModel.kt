package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
open class BaseModel(
    @Transient open val id: String,
    @Transient open val created: Date?
): Parcelable {
    val createdAt: String
        get() {
            return DateHelper.format(created, "MMM d, yyyy") ?: "Never"
        }
}