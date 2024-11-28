package org.lemonadestand.btb.features.dashboard.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.radio.RadioGroup
import org.lemonadestand.btb.core.models.Post

class SelectVisibilityView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0,
	val filter: Post.Visibility
) : RelativeLayout(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = SelectVisibilityView::class.java.simpleName
	}

	var onSelect: ((value: Post.Visibility) -> Unit)? = null

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_dashboard_filter, this, true)

		val filters = arrayListOf(Post.Visibility.ORGANIZATION, Post.Visibility.MINE, Post.Visibility.RESOURCE, Post.Visibility.PUBLIC)
		val visibilityRadioGroup = findViewById<RadioGroup>(R.id.visibilityRadioGroup)
		visibilityRadioGroup.selection = filters.indexOfFirst { x -> x === filter }
		visibilityRadioGroup.onSelect = { onSelect?.invoke(filters[it]) }
	}
}