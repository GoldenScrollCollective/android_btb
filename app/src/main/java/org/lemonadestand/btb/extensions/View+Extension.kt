package org.lemonadestand.btb.extensions

import android.view.View

class OnSingleClickListener : View.OnClickListener {

	private val onClickListener: View.OnClickListener

	constructor(listener: View.OnClickListener) {
		onClickListener = listener
	}

	constructor(listener: (View) -> Unit) {
		onClickListener = View.OnClickListener { listener.invoke(it) }
	}

	override fun onClick(v: View) {
		val currentTimeMillis = System.currentTimeMillis()

		if (currentTimeMillis >= previousClickTimeMillis + DELAY_MILLIS) {
			previousClickTimeMillis = currentTimeMillis
			onClickListener.onClick(v)
		}
	}

	companion object {
		// Tweak this value as you see fit. In my personal testing this
		// seems to be good, but you may want to try on some different
		// devices and make sure you can't produce any crashes.
		private const val DELAY_MILLIS = 200L

		private var previousClickTimeMillis = 0L
	}
}

fun View.setOnSingleClickListener(l: View.OnClickListener) {
	setOnClickListener(OnSingleClickListener(l))
}

fun View.setOnSingleClickListener(l: (View) -> Unit) {
	setOnClickListener(OnSingleClickListener(l))
}

fun View.hide() {
	visibility = View.GONE
}

fun View.show() {
	visibility = View.VISIBLE
}


fun View.invisible() {
	visibility = View.INVISIBLE
}

fun View.enable() {
	isEnabled = true
	alpha = 1.0f
}

fun View.disable() {
	isEnabled = false
	alpha = 0.5f
}