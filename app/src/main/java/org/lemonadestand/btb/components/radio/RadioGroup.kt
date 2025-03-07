package org.lemonadestand.btb.components.radio

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.RelativeLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.extensions.setOnSingleClickListener

class RadioGroup @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
	companion object {
		val TAG: String = this::class.java.simpleName
	}

	var onSelect: ((value: Int) -> Unit)? = null

	private lateinit var buttons: ArrayList<RadioButton>
	private lateinit var dividers: ArrayList<RelativeLayout>

	var selection = 0
		set(value) {
			field = value
			handleSelection()
		}

	init {
		attrs?.let { it ->
			val attributes = context.obtainStyledAttributes(it, R.styleable.RadioGroup)

			selection = attributes.getInteger(R.styleable.RadioGroup_selection, 0)

			attributes.recycle()
		}
	}

	override fun onAttachedToWindow() {
		super.onAttachedToWindow()

		buttons = arrayListOf()
		dividers = arrayListOf()
		for (i in 0 until childCount) {
			val child = getChildAt(i)
			if (child is RadioButton) {
				buttons.add(child)
			} else if (child is RelativeLayout) {
				dividers.add(child)
			}
		}
		for (i in 0 until buttons.size) {
			val button = buttons[i]
			button.setOnSingleClickListener {
				selection = i
				onSelect?.invoke(i)
			}
		}
		handleSelection()
	}

	private fun handleSelection() {
		if (!this::buttons.isInitialized) return
		if (!this::dividers.isInitialized) return

		for (i in 0 until buttons.size) {
			val button = buttons[i]
			button.isSelected = i == selection
		}

		for (i in 0 until dividers.size) {
			val divider = dividers[i]
			divider.visibility = if (selection - i in 0..1) GONE else VISIBLE
		}
	}
}
