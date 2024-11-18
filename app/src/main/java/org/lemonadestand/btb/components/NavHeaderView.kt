package org.lemonadestand.btb.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.databinding.LayoutNavHeaderBinding
import org.lemonadestand.btb.extensions.setOnSingleClickListener

class NavHeaderView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0
): RelativeLayout(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = NavHeaderView::class.java.simpleName
	}

	private lateinit var btnLeft: RelativeLayout
	private lateinit var btnRight: AppCompatTextView

	var onLeftPressed: (() -> Unit)? = null
		set(value) {
			field = value
			btnLeft.visibility = if (value != null) VISIBLE else GONE
			btnLeft.setOnSingleClickListener { value?.invoke() }
		}
	var onRightPressed: (() -> Unit)? = null
		set(value) {
			field = value
			btnRight.visibility = if (value != null) VISIBLE else GONE
			btnRight.setOnSingleClickListener { value?.invoke() }
		}

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_nav_header, this, true)

		attrs?.let { it ->
			val attributes = context.obtainStyledAttributes(it, R.styleable.NavHeaderView)

			val titleView = findViewById<AppCompatTextView>(R.id.titleView)
			titleView.text = attributes.getString(R.styleable.NavHeaderView_title)

			btnLeft = findViewById(R.id.btnLeft)

			btnRight = findViewById(R.id.btnRight)
			btnRight.visibility = GONE

			attributes.recycle()
		}
	}
}