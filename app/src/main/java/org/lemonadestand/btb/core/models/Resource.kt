package org.lemonadestand.btb.core.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class Resource(
	override val id: String,
	override val name: String,
	override val picture: String? = null,
	val interests: ArrayList<Interest>? = null
) : BasePictureModel(id, name, picture)

enum class EventFor(val value: String) {
	team("user"),
	contact("contact"),
	company("company"),
}