package org.lemonadestand.btb.extensions

import java.util.Locale

fun String.lastPathComponent() = substring(lastIndexOf("/") + 1)

fun String.getImageUrlFromName(): String {
	var name = this.lowercase(Locale.ROOT)
	if (!this.contains(" ")) {
		name = this.substring(0, 1) + " " + this.substring(1)
	}
	return "https://ui-avatars.com/api/?name=${name}"
}

fun String.decodeUnicode(): String {
	val regex = Regex("\\\\u([0-9A-Fa-f]{4})")
	return regex.replace(this) {
		val code = it.groupValues[1].toInt(16)
		code.toChar().toString()
	}
}