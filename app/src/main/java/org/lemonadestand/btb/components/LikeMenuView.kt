package org.lemonadestand.btb.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.extenstions.setOnSingleClickListener

class LikeMenuView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0
): LinearLayout(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = LikeMenuView::class.java.simpleName
	}

	var onLike: ((value: String) -> Unit)? = null

	init {
		LayoutInflater.from(context).inflate(R.layout.custom_like_menu, this, true)

		val like = findViewById<ImageView>(R.id.ic_like)
		like.setOnSingleClickListener { onLike?.invoke("like") }

		val love = findViewById<ImageView>(R.id.ic_love)
		love.setOnSingleClickListener { onLike?.invoke("love") }

		val awesome = findViewById<ImageView>(R.id.ic_awesome)
		awesome.setOnSingleClickListener { onLike?.invoke("awesome") }

		val thanks = findViewById<ImageView>(R.id.ic_thanks)
		thanks.setOnSingleClickListener { onLike?.invoke("thanks") }

		val haha = findViewById<ImageView>(R.id.ic_haha)
		haha.setOnSingleClickListener { onLike?.invoke("haha") }
	}
}