package org.lemonadestand.btb.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.features.post.models.Post
import org.lemonadestand.btb.features.post.models.User

class ReactionsDetailDialog @JvmOverloads constructor(
    context: Context,
    val post: Post
): Dialog(context) {
    companion object {
        val TAG: String = ReactionsDetailDialog::class.java.simpleName
    }

    private lateinit var titleView: TextView
    private lateinit var usersRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.layout_reactions_detail)

        init()
    }

    private fun init() {
        titleView = findViewById(R.id.titleView)

        usersRecyclerView = findViewById(R.id.recyclerView)
        usersRecyclerView.adapter = UsersRecyclerViewAdapter()

        handlePost()
    }

    private fun handlePost() {

        val users = ArrayList<User>()
        post.meta.like?.let { metaLike ->
            if (metaLike.find { x -> x.value == "like" } != null) {
                titleView.text = "like"
            }

            if (metaLike.find { x -> x.value == "love" } != null) {
                titleView.text = "love"
            }

            if (metaLike.find { x -> x.value == "awesome" } != null) {
                titleView.text = "awesome"
            }

            if (metaLike.find { x -> x.value == "haha" } != null) {
                titleView.text = "haha"
            }

            for (bonus in metaLike) {
                users.add(bonus.by_user)
            }
        }

        (usersRecyclerView.adapter as UsersRecyclerViewAdapter).values = users
    }

    private class UsersRecyclerViewAdapter(): BaseRecyclerViewAdapter<User>(R.layout.layout_reactions_detail_item) {
        override fun bindView(holder: ViewHolder, item: User, position: Int) {
            super.bindView(holder, item, position)

            with(holder.itemView) {
                val avatarView = findViewById<ImageView>(R.id.avatarView)
                if (item.picture != null) {
                    Glide.with(context).load(item.picture).into(avatarView)
                } else {
                    item.name?.let {
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