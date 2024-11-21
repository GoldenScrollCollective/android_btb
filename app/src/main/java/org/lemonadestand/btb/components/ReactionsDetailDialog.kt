package org.lemonadestand.btb.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.models.User

class ReactionsDetailDialog @JvmOverloads constructor(
	context: Context,
	val post: Post
) : Dialog(context) {
	companion object {
		val TAG: String = ReactionsDetailDialog::class.java.simpleName
	}

	private lateinit var recyclerView: RecyclerView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)

		setContentView(R.layout.layout_reactions_detail)

		init()
	}

	private fun init() {
		recyclerView = findViewById(R.id.recyclerView)
		recyclerView.adapter = ReactionsRecyclerViewAdapter()

		handlePost()
	}

	private fun handlePost() {
		val reactions = ArrayList<Reaction>()
		post.meta.like?.let { metaLike ->
			val values = arrayListOf("like", "love", "awesome", "haha", "thanks")
			for (value in values) {
				val likes = metaLike.filter { x -> x.value == value }
				if (likes.isNotEmpty()) {
					reactions.add(Reaction(title = value, users = ArrayList(likes.map { x -> x.by_user })))
				}
			}
		}
		(recyclerView.adapter as ReactionsRecyclerViewAdapter).values = reactions
	}

	private class Reaction(
		val title: String,
		val users: ArrayList<User>
	)

	private class ReactionsRecyclerViewAdapter : BaseRecyclerViewAdapter<Reaction>(R.layout.layout_reactions_detail_item) {
		override fun bindView(holder: ViewHolder, item: Reaction, position: Int) {
			super.bindView(holder, item, position)

			with(holder.itemView) {
				val titleView = findViewById<TextView>(R.id.titleView)
				titleView.text = item.title

				val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
				recyclerView.adapter = UsersRecyclerViewAdapter()
				(recyclerView.adapter as UsersRecyclerViewAdapter).values = item.users
			}
		}
	}

	private class UsersRecyclerViewAdapter : BaseRecyclerViewAdapter<User>(R.layout.layout_reactions_detail_user_item) {
		override fun bindView(holder: ViewHolder, item: User, position: Int) {
			super.bindView(holder, item, position)

			with(holder.itemView) {
				val avatarView = findViewById<ImageView>(R.id.avatarView)
				if (item.picture != null) {
					Glide.with(context).load(item.picture).into(avatarView)
				} else {
					item.name.let {
						Glide.with(context).load(it.trim().lowercase().getImageUrlFromName())
							.into(avatarView)
					}
				}

				val userNameView = findViewById<TextView>(R.id.userNameView)
				userNameView.text = item.name
			}
		}
	}
}