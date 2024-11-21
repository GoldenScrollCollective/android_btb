package org.lemonadestand.btb.features.post.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.CommentReactionsView
import org.lemonadestand.btb.components.LikeMenuView
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.post.fragments.CompanyTabFragment


class PostCommentsRecyclerViewAdapter(
	private var superPosition: Int
) : BaseRecyclerViewAdapter<Post>(R.layout.layout_post_comment_list_item, true) {
	var onLike: ((post: Post, value: String) -> Unit)? = null
	var onDelete: ((value: Post) -> Unit)? = null

	override fun bindView(holder: ViewHolder, item: Post, position: Int) {
		super.bindView(holder, item, position)

		with(holder.itemView) {
			val avatarView = findViewById<ImageView>(R.id.avatarView)
			if (item.byUser.picture != null) {
				Glide.with(context).load(item.user.picture).into(avatarView)
			} else {
                item.byUser.name.let {
                    Glide.with(context).load(it.trim().lowercase().getImageUrlFromName())
                        .into(avatarView)
                }
			}

			val userNameView = findViewById<TextView>(R.id.userNameView)
			userNameView.text = item.user.name

			val commentView = findViewById<TextView>(R.id.commentView)
			commentView.text = HtmlCompat.fromHtml(item.html, HtmlCompat.FROM_HTML_MODE_COMPACT)

			val agoView = findViewById<TextView>(R.id.agoView)
			agoView.text = item.createdAgo

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
				popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
						CompanyTabFragment.viewModel.addComment(requestBody)
					}
					.setNegativeButton("Cancel") { dialog, _ ->
						dialog.dismiss() // Dismiss the dialog if canceled
					}
					.show() // Show the dialog


//                if (item.replies.isEmpty()) {
//                    val json = Gson().toJson(item)
//                    (context as Activity).launchActivity<ReplyCommentActivity> {
//                        putExtra("reply_data", json)
//                    }
//                    return@setOnSingleClickListener
//                }

//                val popupMenu = PopupMenu(context, btnReply)
//                popupMenu.menuInflater.inflate(R.menu.comment_menu, popupMenu.menu)
//                popupMenu.show()
//                popupMenu.setOnMenuItemClickListener { menuItem ->
//                    when (menuItem.itemId) {
//                        R.id.add_comment -> {
//                            val json = Gson().toJson(item)
//                            (context as Activity).launchActivity<ReplyCommentActivity> {
//                                putExtra("reply_data", json)
//                            }
//                            true
//                        }
//
//                        R.id.view_comment -> {
//
//                            if (item.replies.isNotEmpty()) {
//                                val json = Gson().toJson(item.replies)
//                                val intent = Intent(context, AddBonusActivity::class.java)
//                                intent.putExtra("list", json)
//                                context.startActivity(intent)
//                            } else {
//                                val json = Gson().toJson(item)
//                                (context as Activity).launchActivity<ReplyCommentActivity> {
//                                    putExtra("reply_data", json)
//                                }
//                            }
//
//
//                            true
//                        }
//
//                        else -> false
//                    }
//                }
			}

			val reactionsView = findViewById<CommentReactionsView>(R.id.reactionsView)
			reactionsView.post = item

			val repliesRecyclerView = findViewById<RecyclerView>(R.id.repliesRecyclerView)
			val repliesRecyclerViewAdapter = PostCommentsRecyclerViewAdapter(position)
			repliesRecyclerViewAdapter.onLike = { post, value -> onLike?.invoke(post, value) }
			repliesRecyclerView.adapter = repliesRecyclerViewAdapter
			repliesRecyclerViewAdapter.values = item.replies
		}
	}
}