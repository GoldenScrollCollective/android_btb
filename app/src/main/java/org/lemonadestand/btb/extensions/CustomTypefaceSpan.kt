package org.lemonadestand.btb.extensions

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class CustomTypefaceSpan(private val newType: Typeface?) : TypefaceSpan("") {
	override fun updateDrawState(ds: TextPaint) {
		applyCustomTypeFace(ds, newType)
	}

	override fun updateMeasureState(paint: TextPaint) {
		applyCustomTypeFace(paint, newType)
	}

	private fun applyCustomTypeFace(paint: Paint, tf: Typeface?) {
		paint.typeface = tf
	}
}