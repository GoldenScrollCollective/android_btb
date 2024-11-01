package org.lemonadestand.btb.features.post.adapter

import android.app.AlertDialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.extenstions.ago
import org.lemonadestand.btb.extenstions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.post.fragments.PublicFragment
import org.lemonadestand.btb.features.post.models.Post
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.utils.Utils


class PostCommentsRecyclerViewAdapter(private var superPosition: Int, private var onPostItemClick: OnItemClickListener?): BaseRecyclerViewAdapter<Post>(R.layout.layout_post_comment_list_item, true) {
//    private var onPostItemClick: OnItemClickListener? = null
    fun setPostItemClick(listener: OnItemClickListener) { onPostItemClick = listener }

    override fun bindView(holder: ViewHolder, item: Post, position: Int) {
        super.bindView(holder, item, position)

        with(holder.itemView) {
            val avatarView = findViewById<ImageView>(R.id.avatarView)
            if (item.by_user.picture != null) {
                Glide.with(context).load(item.user.picture).into(avatarView)
            } else {
                item.by_user.name?.let {
                    Glide.with(context).load(it.trim().lowercase().getImageUrlFromName())
                        .into(avatarView)
                }
            }

            val userNameView = findViewById<TextView>(R.id.userNameView)
            userNameView.text = item.user.name

            val commentView = findViewById<TextView>(R.id.commentView)
            commentView.text = HtmlCompat.fromHtml(item.html, HtmlCompat.FROM_HTML_MODE_COMPACT)

            val agoView = findViewById<TextView>(R.id.agoView)
            item.createdAt()?.let { agoView.text = it.ago() }

            val btnLike = findViewById<TextView>(R.id.btnLike)
            btnLike.setOnSingleClickListener {
                val view: View = LayoutInflater.from(context).inflate(R.layout.custom_like_menu, null)
                val popupWindow = PopupWindow(
                    view,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                val resources: Resources = context.resources
                val like = view.findViewById<ImageView>(R.id.ic_like)
                val love = view.findViewById<ImageView>(R.id.ic_love)
                val awesome = view.findViewById<ImageView>(R.id.ic_awesome)
                val thanks = view.findViewById<ImageView>(R.id.ic_thanks)
                val haha = view.findViewById<ImageView>(R.id.ic_haha)

                like.setOnClickListener {
                    popupWindow.dismiss()

                    onPostItemClick?.onItemClicked(
                        LikeBodyModel(metaName = "like", metaValue = "like", byUserId = Utils.getData(context,
                        Utils.UID), uniqueId = item.uniq_id), position,
                        ClickType.LIKE_POST, superIndex = superPosition)


                }
                love.setOnClickListener {
                    popupWindow.dismiss()
                    onPostItemClick?.onItemClicked(
                        LikeBodyModel(metaName = "like", metaValue = "love", byUserId = Utils.getData(context,
                        Utils.UID), uniqueId = item.uniq_id),position,
                        ClickType.LIKE_POST,superIndex = superPosition)
                }
                awesome.setOnClickListener {
                    popupWindow.dismiss()
                    onPostItemClick?.onItemClicked(
                        LikeBodyModel(metaName = "like", metaValue = "awesome", byUserId = Utils.getData(context,
                        Utils.UID), uniqueId = item.uniq_id),position,
                        ClickType.LIKE_POST,superIndex = superPosition)
                }

                thanks.setOnClickListener {
                    popupWindow.dismiss()
                    onPostItemClick?.onItemClicked(
                        LikeBodyModel(metaName = "like", metaValue = "thanks", byUserId = Utils.getData(context,
                        Utils.UID), uniqueId = item.uniq_id),position,
                        ClickType.LIKE_POST,superIndex = superPosition)
                }
                haha.setOnClickListener {
                    popupWindow.dismiss()
                    onPostItemClick?.onItemClicked(
                        LikeBodyModel(metaName = "like", metaValue = "haha", byUserId = Utils.getData(context,
                        Utils.UID), uniqueId = item.uniq_id),position,
                        ClickType.LIKE_POST,superIndex = superPosition)
                }


                val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                popupWindow.isFocusable = true
                popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                popupWindow.showAsDropDown(it, 100, 0, 0)
            }

            val btnReply = findViewById<TextView>(R.id.btnReply)
            btnReply.setOnSingleClickListener {

                val title = TextView(context) //custom title
                title.setText("Add Reply")
                title.setPadding(0, 50, 0, 0)
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                title.gravity = Gravity.CENTER

                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.setPadding(50, 0, 50, 0)

                val messageArea = TextView(context)
                messageArea.setText("What would you like to say...")
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
                            resource = "user/${PublicFragment.currentUser!!.uniqId}",
                            html = message,
                            created = "",
                            parent_id = "${item?.uniq_id}",
                            modified = "",
                            by_user_id = "",

                            user = ShareStoryUser(id = "", name = "")
                        )

//                    viewModel.addComment(requestBody)
                        PublicFragment.viewModel.addComment(requestBody)
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

            val likeCountView = findViewById<TextView>(R.id.likeCountView)
            likeCountView.text = "${item.meta.like?.size}"

            val likeIconView = findViewById<ImageView>(R.id.likeIconView)
            if ((item.meta.like?.size ?: 0) > 0) {
                likeIconView.setImageResource(R.drawable.ic_like_up)
            } else {
                likeIconView.setImageResource(R.drawable.ic_like)
            }

            val repliesRecyclerView = findViewById<RecyclerView>(R.id.repliesRecyclerView)
            val repliesRecyclerViewAdapter = PostCommentsRecyclerViewAdapter(position, onPostItemClick)
            onPostItemClick?.let { setPostItemClick(it) }
            repliesRecyclerView.adapter = repliesRecyclerViewAdapter
            repliesRecyclerViewAdapter.values = item.replies
        }
    }
}