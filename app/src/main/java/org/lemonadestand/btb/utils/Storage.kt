package org.lemonadestand.btb.utils

import android.content.Context
import org.lemonadestand.btb.App

object Storage {
	private val sharedPreferences = App.instance.getSharedPreferences("btb-preferences", Context.MODE_PRIVATE)

	private const val eventsTabIndexKey = "eventsTabIndexKey"
	var eventsTabIndex: Int
		get() = sharedPreferences.getInt(eventsTabIndexKey, 0)
		set(value) {
			val editor = sharedPreferences.edit()
			editor.putInt(eventsTabIndexKey, value)
			editor.apply()
		}

	private const val rawTokenKey = "rawToken"
	var rawToken: String
		get() = sharedPreferences.getString(rawTokenKey, "").toString()
		set(value) {
			val editor = sharedPreferences.edit()
			editor.putString(rawTokenKey, value)
			editor.apply()
		}
}