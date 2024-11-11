package org.lemonadestand.btb.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.lemonadestand.btb.R
import org.lemonadestand.btb.features.post.models.Post

class ReactionsView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0
): LinearLayout(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = ReactionsView::class.java.simpleName
	}

	private val thumbView: ImageView
	private val heartView: ImageView
	private val fireView: ImageView
	private val prayView: ImageView
	private val laughView: ImageView
	private val countView: TextView

	var post: Post? = null
		set(value) {
			field = value
			handlePost()
		}

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_reactions_view, this, true)

		thumbView = findViewById(R.id.thumbView)
		heartView = findViewById(R.id.heartView)
		fireView = findViewById(R.id.fireView)
		prayView = findViewById(R.id.prayView)
		laughView = findViewById(R.id.laughView)
		countView = findViewById(R.id.countView)
	}

	private fun handlePost() {
		thumbView.visibility = GONE
		heartView.visibility = GONE
		fireView.visibility = GONE
		prayView.visibility = GONE
		laughView.visibility = GONE
		countView.visibility = GONE

		if (post?.meta == null || (post?.meta?.like?.size ?: 0) <= 0) {
			visibility = GONE
			return
		}

		visibility = VISIBLE

		val meta = post?.meta ?: return
		meta.like?.let { metaLike ->
			countView.visibility = VISIBLE
			countView.text = "${metaLike.size}"

			if (metaLike.find { x -> x.value == "like" } != null || metaLike.size == 0) {
				thumbView.visibility = VISIBLE
			}

			if (metaLike.find { x -> x.value == "love" } != null) {
				heartView.visibility = VISIBLE
			}

			if (metaLike.find { x -> x.value == "awesome" } != null) {
				fireView.visibility = VISIBLE
			}

			if (metaLike.find { x -> x.value == "haha" } != null) {
				laughView.visibility = VISIBLE
			}

			if (metaLike.find { x -> x.value == "thanks" } != null) {
				prayView.visibility = VISIBLE
			}
		}

		setOnClickListener {
			val detailDialog = ReactionsDetailDialog(context, post!!)
			detailDialog.show()
		}
	}
}