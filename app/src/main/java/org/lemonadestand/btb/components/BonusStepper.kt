package org.lemonadestand.btb.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.withStyledAttributes
import org.lemonadestand.btb.R
import org.lemonadestand.btb.extensions.disable
import org.lemonadestand.btb.extensions.enable
import kotlin.math.max
import kotlin.math.min

class BonusStepper @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
) : CardView(context, attrs, defStyle) {
	companion object {
		val TAG: String = this::class.java.simpleName
	}

	private val btnMinus: TextView
	private val btnPlus: TextView

	var min: Double = 0.0
		set(value) {
			field = value
			this.value = max(min, this.value)
		}
	var max: Double = 0.0
		set(value) {
			field = value
			this.value = min(max, this.value)
		}
	var step = 1.0
	var value: Double = min
		set(value) {
			field = value

			btnMinus.apply {
				if (this@BonusStepper.value <= min) disable()
				else enable()
			}

			btnPlus.apply {
				if (this@BonusStepper.value >= max) disable()
				else enable()
			}
		}

	var onChange: ((value: Double) -> Unit)? = null

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_bonus_stepper, this, true)

		attrs?.let { it ->
			context.withStyledAttributes(it, R.styleable.NavHeaderView) {
				step = getInteger(R.styleable.BonusStepper_step, 1).toDouble()
			}
		}

		btnMinus = findViewById(R.id.btnMinus)
		btnMinus.setOnClickListener {
			value = max(min, value - step)
			onChange?.invoke(value)
		}

		btnPlus = findViewById(R.id.btnPlus)
		btnPlus.setOnClickListener {
			value = min(max, value + step)
			onChange?.invoke(value)
		}
	}
}
