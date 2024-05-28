package org.lemonadestand.btb.features.post.adapter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chantsune.swipetoaction.views.SimpleSwipeLayout
import com.google.gson.Gson
import org.lemonadestand.btb.R
import org.lemonadestand.btb.features.post.activities.ReplyCommentActivity
import org.lemonadestand.btb.features.post.activities.AddBonusActivity
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.features.post.models.PostModel
import org.lemonadestand.btb.singleton.Singleton.launchActivity


class CommentSubAdapter(private val list: ArrayList<PostModel>, var context: Context) :
    RecyclerView.Adapter<CommentSubAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.row_comment_sub, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]



        if (data.by_user.picture != null) {
            // show image
            Glide.with(context).load(data.by_user.picture).into(holder.userImage)
        } else {

            Log.e("url=>", data.by_user.name!!.trim().lowercase().getImageUrlFromName())
            Glide.with(context).load(data.by_user.name.trim().lowercase().getImageUrlFromName())
                .into(holder.userImage)
        }


        holder.tvTitle.text = data.by_user.name






        holder.tvComment.text = HtmlCompat.fromHtml(data.html, HtmlCompat.FROM_HTML_MODE_LEGACY)

        if (data.replies.isNotEmpty()) {
            holder.txtCommentCount.text = buildString {
                append("Comment")
                append("(")
                append(data.replies.size)
                append(")")
            }
        }

        if (!data.meta.like.isNullOrEmpty()) {
            holder.txtLikeCount.text = buildString {
                append("Like")
                append("(")
                append(data.meta.like.size)
                append(")")
            }
        }



        holder.tvTitle.setOnClickListener {


        }

        holder.lnComment.setOnClickListener {

            if (data.replies.isEmpty()) {
                val json = Gson().toJson(data)
                (context as Activity).launchActivity<ReplyCommentActivity> {
                    putExtra("reply_data", json)
                }
                return@setOnClickListener
            }


            val popupMenu = PopupMenu(context, holder.lnComment)
            popupMenu.menuInflater.inflate(R.menu.comment_menu, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.add_comment -> {
                        val json = Gson().toJson(data)
                        (context as Activity).launchActivity<ReplyCommentActivity> {
                            putExtra("reply_data", json)
                        }
                        true
                    }

                    R.id.view_comment -> {
                        if (data.replies.isNotEmpty()) {
                            val json = Gson().toJson(data.replies)
                            val intent = Intent(context, AddBonusActivity::class.java)
                            intent.putExtra("list", json)
                            context.startActivity(intent)
                        } else {
                            val json = Gson().toJson(data)
                            (context as Activity).launchActivity<ReplyCommentActivity> {
                                putExtra("reply_data", json)
                            }
                        }
                        true
                    }

                    else -> false
                }
            }


        }



        holder.lnLike.setOnClickListener {

            val view: View = LayoutInflater.from(context).inflate(R.layout.custom_like_menu, null)
            val popupWindow = PopupWindow(
                view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val resources: Resources = context.resources
            val resourceId: Int =
                resources.getIdentifier("navigation_bar_height", "dimen", "android")
            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.showAsDropDown(it, 100, 0, 0)
            // popupWindow.showAsDropDown(it, Gravity.TOP or Gravity.FILL_HORIZONTAL, 0,it.bottom)
        }


        /* holder.tv_title.setText(data.getName());
        holder.tv_desc.setText(data.getDesc());

        Glide.with(context)
                .load(data.getImage())
                .into(holder.iv_image);*/

        holder.swipeLayout.setOnSwipeItemClickListener { swipeItem ->

            when (swipeItem.position) {
                0 -> {
                    Toast.makeText(holder.itemView.context, "Delete", Toast.LENGTH_SHORT)
                        .show()
                }

                1 -> {
                    Toast.makeText(holder.itemView.context, "Settings", Toast.LENGTH_SHORT)
                        .show()
                }

                2 -> {
                    val pos = holder.absoluteAdapterPosition

                    notifyItemRemoved(pos)
                    Toast.makeText(holder.itemView.context, "Trash", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView


        var lnLike: LinearLayout
        var lnComment: LinearLayout
        var swipeLayout: SimpleSwipeLayout
        var tvComment: TextView

        var userImage: ImageView

        var txtLikeCount: TextView
        var txtCommentCount: TextView


        init {
            tvTitle = itemView.findViewById(R.id.tv_title)

            lnLike = itemView.findViewById(R.id.ln_like)
            lnComment = itemView.findViewById(R.id.ln_comment)
            swipeLayout = itemView.findViewById(R.id.swipe_layout)
            tvComment = itemView.findViewById(R.id.tv_comment)
            userImage = itemView.findViewById(R.id.user_image)
            txtLikeCount = itemView.findViewById(R.id.txt_like)
            txtCommentCount = itemView.findViewById(R.id.txt_comment)
        }
    }
}