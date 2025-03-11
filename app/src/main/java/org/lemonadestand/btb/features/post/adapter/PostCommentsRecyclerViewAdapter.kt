package org.lemonadestand.btb.features.post.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.CommentReactionsView
import org.lemonadestand.btb.components.LikeMenuView
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.core.manager.PostsManager
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.extensions.CustomTypefaceSpan
import org.lemonadestand.btb.extensions.setImageUrl
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.post.fragments.CompanyTabFragment


class PostCommentsRecyclerViewAdapter : BaseRecyclerViewAdapter<Post>(R.layout.layout_post_comment_list_item, true) {
	var onLike: ((post: Post, value: String) -> Unit)? = null
	var onDelete: ((value: Post) -> Unit)? = null

	override fun bindView(holder: ViewHolder, item: Post, position: Int) {
		super.bindView(holder, item, position)

		with(holder.itemView) {
			val avatarView = findViewById<ImageView>(R.id.avatarView)
			avatarView.setImageUrl(item.user.pictureUrl)

			val mediumFont = ResourcesCompat.getFont(context, R.font.urbanist_medium)
			val boldFont = ResourcesCompat.getFont(context, R.font.urbanist_bold)

			val userNameView = findViewById<TextView>(R.id.userNameView)
			item.byUser?.let { resource ->
				val name = "${resource.name} · "
				val ago = item.createdAgo ?: ""
				val username = "$name$ago"
				userNameView.text = SpannableStringBuilder(username).apply {
					setSpan(AbsoluteSizeSpan(16, true), 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
					setSpan(CustomTypefaceSpan(boldFont), 0, name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

					setSpan(AbsoluteSizeSpan(12, true), name.length, username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
					setSpan(CustomTypefaceSpan(mediumFont), name.length, username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
				}
			}

			val commentView = findViewById<TextView>(R.id.commentView)
			val html = item.html.replace("</p><p>", "<br>").replace("<p>", "").replace("</p>", "")
			commentView.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)

			val btnLike = findViewById<TextView>(R.id.btnLike)
			btnLike.setOnSingleClickListener {
				val likeMenuView = LikeMenuView(context)

				val popupWindow = PopupWindow(
					likeMenuView,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT
				)
				likeMenuView.onLike = { value ->
					popupWindow.dismiss()
					onLike?.invoke(item, value)
				}

				popupWindow.isFocusable = true
				popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
				popupWindow.showAsDropDown(it, 0, 0, 0)
			}

			val btnReply = findViewById<TextView>(R.id.btnReply)
			btnReply.setOnSingleClickListener {

				val title = TextView(context) //custom title
				title.text = "Add Reply"
				title.setPadding(0, 50, 0, 0)
				title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
				title.gravity = Gravity.CENTER

				val layout = LinearLayout(context)
				layout.orientation = LinearLayout.VERTICAL
				layout.setPadding(50, 0, 50, 0)

				val messageArea = TextView(context)
				messageArea.text = "What would you like to say..."
				messageArea.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
				messageArea.gravity = Gravity.CENTER

				layout.addView(messageArea)

				val input = EditText(context)
				input.hint = "Text"
				val layoutParams = LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT
				)
				layoutParams.setMargins(0, 20, 0, 0)
				input.layoutParams = layoutParams

				val textIndent = context.resources.getDimensionPixelOffset(R.dimen.fab_margin)
				input.setPadding(textIndent, 20, 0, 20)

				val shape = GradientDrawable()
				shape.setStroke(2, ContextCompat.getColor(context, R.color.text_grey))
				shape.cornerRadius = context.resources.getDimension(R.dimen.fab_margin)
				shape.setColor(ContextCompat.getColor(context, R.color.bottom_navigation_color))
				input.background = shape

				layout.addView(input)

				AlertDialog.Builder(context)
					.setCustomTitle(title) // Set dialog title
					.setView(layout)
					.setPositiveButton("OK") { _, _ ->
						val message = input.text.toString() // Get the input message
//                    // Handle the confirmation with the input message

						val requestBody = AddCommentBody(
							uniq_id = "",
							resource = "user/${CompanyTabFragment.currentUser!!.uniqueId}",
							html = message,
							created = "",
							parent_id = "${item.uniqueId}",
							modified = "",
							by_user_id = "",

							user = ShareStoryUser(id = "", name = "")
						)

//                    viewModel.addComment(requestBody)
						PostsManager.addComment(requestBody)
					}
					.setNegativeButton("Cancel") { dialog, _ ->
						dialog.dismiss() // Dismiss the dialog if canceled
					}
					.show() // Show the dialog
			}

			val reactionsView = findViewById<CommentReactionsView>(R.id.reactionsView)
			reactionsView.post = item

			val bonusView = findViewById<LinearLayout>(R.id.bonusView)
			val bonusImageView = findViewById<ImageView>(R.id.bonusImageView)
			val bonusTextView = findViewById<TextView>(R.id.bonusTextView)
			if (item.bonus.isNullOrEmpty() || item.bonus == "0") {
				bonusView.visibility = View.GONE
			} else {
				bonusView.visibility = View.VISIBLE
				if (item.debit == "spend") {
					bonusImageView.setImageResource(R.drawable.ic_money_love)
				} else {
					bonusImageView.setImageResource(R.drawable.ic_money)
				}
				bonusTextView.text = item.bonus
			}

			val repliesRecyclerView = findViewById<RecyclerView>(R.id.repliesRecyclerView)
			val repliesRecyclerViewAdapter = PostCommentsRecyclerViewAdapter()
			repliesRecyclerViewAdapter.onLike = { post, value -> onLike?.invoke(post, value) }
			repliesRecyclerView.adapter = repliesRecyclerViewAdapter
			repliesRecyclerViewAdapter.values = item.replies
		}
	}
}