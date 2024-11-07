package org.lemonadestand.btb.core.models

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.constants.getImageUrlFromName

@Parcelize
open class BaseModel(
    @Transient open val name: String?,
    @Transient open val picture: String?,
    @Transient open val created: String?
): Parcelable {
    val pictureUrl: String?
        get() {
            if (picture != null) return picture
            if (name == null) return ""
            return name!!.trim().lowercase().getImageUrlFromName()
        }
    val createdAt: String
        get() {
            val date = DateHelper.parse(created, "yyyy-MM-dd HH:mm:ss") ?: return "Never"
            return DateHelper.format(date, "MMM d, yyyy") ?: "Never"
        }
}