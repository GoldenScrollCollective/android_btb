package org.lemonadestand.btb.core

import android.os.Parcelable
import com.aisynchronized.helper.DateHelper
import kotlinx.parcelize.Parcelize
import org.lemonadestand.btb.extensions.ago
import java.util.Date

@Parcelize
open class BaseModel(
	@Transient open val id: String,
	@Transient open val created: Date?
) : Parcelable {
	val createdAt: String?
		get() = DateHelper.format(created, "MMM d, yyyy")

	val createdAgo: String?
		get() = created?.ago()
}