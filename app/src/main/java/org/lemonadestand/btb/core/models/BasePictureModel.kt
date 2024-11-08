package org.lemonadestand.btb.core.models

import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.constants.getImageUrlFromName
import java.util.Date

@Parcelize
open class BasePictureModel(
    @Transient override val id: String,
    @Transient open val name: String?,
    @Transient open val picture: String?,
    @Transient override val created: Date?
): BaseModel(id, created) {
    val pictureUrl: String?
        get() {
            if (picture != null) return picture
            if (name == null) return ""
            return name!!.trim().lowercase().getImageUrlFromName()
        }
}