package org.lemonadestand.btb.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import org.lemonadestand.btb.R

class FilterButton @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {
	companion object {
		val TAG: String = this::class.java.simpleName
	}

	init {
		attrs?.let { it ->
			val attributes = context.obtainStyledAttributes(it, R.styleable.FilterButton)

			isSelected = attributes.getBoolean(R.styleable.FilterButton_selected, false)

			attributes.recycle()
		}

		setOnClickListener { view ->
			isSelected = true
		}
	}

	override fun setSelected(selected: Boolean) {
		super.setSelected(selected)

		if (selected) {
			setBackgroundResource(R.drawable.filter_button_selected)
		} else {
			setBackgroundColor(resources.getColor(R.color.transparent))
		}
	}
}
