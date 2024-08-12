package org.lemonadestand.btb.features.post.adapter


import android.app.Activity
import android.content.Context
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
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chantsune.swipetoaction.views.SimpleSwipeLayout
import com.google.gson.Gson
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.R
import org.lemonadestand.btb.utils.Utils
import org.lemonadestand.btb.features.post.activities.ReplyCommentActivity
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.extenstions.ago
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.post.activities.AddBonusActivity
import org.lemonadestand.btb.features.post.models.PostModel
import org.lemonadestand.btb.singleton.Singleton.launchActivity

import android.app.AlertDialog
import android.graphics.drawable.GradientDrawable
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.post.fragments.PublicFragment
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton


class PublicSubAdapter(private val list: ArrayList<PostModel>, var context: Context, private var superPosition: Int) :
    RecyclerView.Adapter<PublicSubAdapter.ViewHolder>() {
    private var onItemClick: OnItemClickListener? = null

    private lateinit var usernameEvent: LinearLayout

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.row_public_sub, parent, false)

        return ViewHolder(listItem)
    }

    fun setItemClick(onItemClickListener: OnItemClickListener)
    {
        this.onItemClick = onItemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list[position]
        Log.e("TAG", "** H " + data.toString())



        if (data.user.picture != null) {
            // show image
            Glide.with(context).load(data.user.picture).into(holder.userImage)
        } else {

            Log.e("url=>", data.user.name!!.trim().lowercase().getImageUrlFromName())
            Glide.with(context).load(data.user.name.trim().lowercase().getImageUrlFromName())
                .into(holder.userImage)
        }

        if (data.by_user.picture != null) {
            // show image
            Glide.with(context).load(data.by_user.picture).into(holder.byUserImage)
        } else {
            holder.cdSubName.visibility = View.INVISIBLE
            Log.e("url=>", data.by_user.name!!.trim().lowercase().getImageUrlFromName())
//            Glide.with(context).load(data.by_user.name.trim().lowercase().getImageUrlFromName())
//                .into(holder.byUserImage)
        }



        if (data.type != null) {
            //comment

            if (data.users.size > 1) {
                holder.tvTitle.text = data.user.name + " +" + (data.users.size - 1)            //Add Uses
            } else {
                holder.tvTitle.text = data.user.name
            }
            holder.txtEventName.text = data.title
            holder.txtShared.text = buildString {
                append("for")
            }
            holder.tvDesc.text = buildString {
                append("by ")
                append(data.by_user.name)
                append(" · ")
                append(data.createdAt()?.let { it.ago() })
            }
        } else {
            //Post
            holder.tvTitle.text = data.user.name
            holder.txtEventName.text = ""
            holder.txtShared.text = buildString {
                append("shared this story · ")
                append(data.createdAt()?.let { it.ago() })
            }
            holder.tvDesc.text = buildString {
                append("")
            };

            holder.lnBonus.visibility = View.GONE
        }

        holder.tvComment.text = HtmlCompat.fromHtml(data.html, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()

        if (data.replies.isNotEmpty()) {
            holder.txtCommentCount.text = buildString {
                append("Comment")
//                append("(")
//                append(data.replies.size)
//                append(")")
            }

            var commentStr = ""
            if (data.replies.size == 1) commentStr = "1 Comment"
            if (data.replies.size > 1) commentStr = "${data.replies.size} Comments"

            holder.txtCommentCount1.text = buildString {
                append(commentStr)
            }
        }

        if (!data.meta.like.isNullOrEmpty()) {
            holder.txtLikeCount.text = buildString {
                append("Like")
//                append("(")
//                append(data.meta.like.size)
//                append(")")
            }

            holder.txtLikeCount1.text = buildString {
                append(data.meta.like.size)
            }

            if (data.meta.like.size != 0) {
                holder.imageLikeMain.setImageResource(R.drawable.ic_like_up)
            }
        }



//        holder.tvTitle.setOnClickListener {
//
//
//        }

        holder.lnBonus.setOnClickListener {

            val json = Gson().toJson(data)
            (context as Activity).launchActivity<AddBonusActivity> {
                putExtra("reply_data", json)
            }
        }

        holder.lnComment.setOnClickListener {
            // add to set alert dialog's styls
            val title = TextView(context) //custom title
            title.setText("Add Comment")
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
                .setCustomTitle(title)
                .setView(layout) // Set the EditText as view in the dialog
                .setPositiveButton("OK") { _, _ ->
                    val message = input.text.toString() // Get the input message
//                    // Handle the confirmation with the input message

                    val requestBody = AddCommentBody(
                        uniq_id = "",
                        resource = "user/${PublicFragment.currentUser!!.uniqId}",
                        html = message,
                        created = "",
                        parent_id = "${data?.uniq_id}",
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

//            val json = Gson().toJson(data)
//            (context as Activity).launchActivity<ReplyCommentActivity> {
//                putExtra("reply_data", json)
//            }
//
//
//            if (data.replies.isEmpty()) {
//                val json = Gson().toJson(data)
//                (context as Activity).launchActivity<ReplyCommentActivity> {
//                    putExtra("reply_data", json)
//                }
//                return@setOnClickListener
//            }
//
//            val popupMenu = PopupMenu(context, holder.lnComment)
//            popupMenu.menuInflater.inflate(R.menu.comment_menu, popupMenu.menu)
//            popupMenu.show()
//            popupMenu.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.add_comment -> {
//                        val json = Gson().toJson(data)
//                        (context as Activity).launchActivity<ReplyCommentActivity> {
//                            putExtra("reply_data", json)
//                        }
//                        true
//                    }
//
//                    R.id.view_comment -> {
//
//                        if (data.replies.isNotEmpty()) {
//                            val json = Gson().toJson(data.replies)
//                            val intent = Intent(context, ViewCommentActivity::class.java)
//                            intent.putExtra("list", json)
//                            context.startActivity(intent)
//                        } else {
//                            val json = Gson().toJson(data)
//                            (context as Activity).launchActivity<ReplyCommentActivity> {
//                                putExtra("reply_data", json)
//                            }
//                        }
//
//
//                        true
//                    }
//
//                    else -> false
//                }
//            }


        }

        holder.tvTitle.setOnClickListener {

            val cardView = CardView(context)
            cardView.setPadding(5, 5, 5, 5)

            val layout2 = LinearLayout(context)
            layout2.orientation = LinearLayout.VERTICAL

            for( i in 0 until data.users.size) {

                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.HORIZONTAL
                layout.setPadding(5, 5, 5, 5)
                layout.elevation = 8f

                val image = ImageView(context)
                //            image.setPadding(30,0,30,0)
                val wrapContentParams = ViewGroup.LayoutParams(
                    50, 50
                )
                image.layoutParams = wrapContentParams
                image.scaleType = ImageView.ScaleType.FIT_XY

                Log.i("Miner=>",
                    data.toString()
                )
                Glide.with(context).load(data.users[i].picture).into(image)

                val messagearea = TextView(context)
                messagearea.setText(data.users[i].name)
                messagearea.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                messagearea.setPadding(20, 0, 10, 0)


                layout.addView(image)
                layout.addView(messagearea)

                layout2.addView(layout)

            }
            cardView.addView(layout2)



            val popupWindow = PopupWindow(
                cardView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )

            popupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.darker_gray))) // Transparent color to remove default shadow
            popupWindow.elevation = 20f

            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            popupWindow.showAsDropDown(it, 100, 0, 0)
        }

        holder.lnLike.setOnClickListener {

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
                onItemClick!!.onItemClicked(LikeBodyModel(metaName = "like", metaValue = "like", byUserId = Utils.getData(context,
                    Utils.UID), uniqueId = data.uniq_id),position,
                    ClickType.LIKE_POST,superIndex = superPosition)
            }
            love.setOnClickListener {
                popupWindow.dismiss()
                onItemClick!!.onItemClicked(LikeBodyModel(metaName = "like", metaValue = "love", byUserId = Utils.getData(context,
                    Utils.UID), uniqueId = data.uniq_id),position,
                    ClickType.LIKE_POST,superIndex = superPosition)
            }
            awesome.setOnClickListener {
                popupWindow.dismiss()
                onItemClick!!.onItemClicked(LikeBodyModel(metaName = "like", metaValue = "awesome", byUserId = Utils.getData(context,
                    Utils.UID), uniqueId = data.uniq_id),position,
                    ClickType.LIKE_POST,superIndex = superPosition)
            }

            thanks.setOnClickListener {
                popupWindow.dismiss()
                onItemClick!!.onItemClicked(LikeBodyModel(metaName = "like", metaValue = "thanks", byUserId = Utils.getData(context,
                    Utils.UID), uniqueId = data.uniq_id),position,
                    ClickType.LIKE_POST,superIndex = superPosition)
            }
            haha.setOnClickListener {
                popupWindow.dismiss()
                onItemClick!!.onItemClicked(LikeBodyModel(metaName = "like", metaValue = "haha", byUserId = Utils.getData(context,
                    Utils.UID), uniqueId = data.uniq_id),position,
                    ClickType.LIKE_POST,superIndex = superPosition)
            }


            val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
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
                    onItemClick!!.onItemClicked(data,position, ClickType.DELETE_POST, superIndex = superPosition)

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

        val commentsRecyclerViewAdapter = PostCommentsRecyclerViewAdapter(position,onItemClick)   // fixed
        onItemClick?.let { commentsRecyclerViewAdapter.setPostItemClick(it) }
        holder.commentsRecyclerView.adapter = commentsRecyclerViewAdapter
        commentsRecyclerViewAdapter.values = data.replies
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView
        var txtEventName: TextView
        var tvDesc: TextView
        var cdSubName: CardView
        var lnLike: LinearLayout
        var lnComment: LinearLayout
        var swipeLayout: SimpleSwipeLayout
        var tvComment: TextView
        var txtShared: TextView
        var userImage: ImageView
        var byUserImage: ImageView
        var txtLikeCount: TextView
        var txtCommentCount: TextView
        var commentsRecyclerView: RecyclerView

        var txtCommentCount1: TextView
        var txtLikeCount1: TextView

        var lnBonus: LinearLayout
        var imageLikeMain: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tv_title)
            tvDesc = itemView.findViewById(R.id.tv_desc)
            txtEventName = itemView.findViewById(R.id.txt_event_name)
            cdSubName = itemView.findViewById(R.id.cd_sub_name)
            lnLike = itemView.findViewById(R.id.ln_like)
            lnComment = itemView.findViewById(R.id.ln_comment)
            swipeLayout = itemView.findViewById(R.id.swipe_layout)
            tvComment = itemView.findViewById(R.id.tv_comment)
            userImage = itemView.findViewById(R.id.user_image)
            txtShared = itemView.findViewById(R.id.txt_shared)
            byUserImage = itemView.findViewById(R.id.by_user_image)
            txtLikeCount = itemView.findViewById(R.id.txt_like)
            txtCommentCount = itemView.findViewById(R.id.txt_comment)
            commentsRecyclerView = itemView.findViewById(R.id.commentsRecyclerView)

            txtCommentCount1 = itemView.findViewById(R.id.txt_comment1)
            txtLikeCount1 = itemView.findViewById(R.id.txt_like1)

            lnBonus = itemView.findViewById(R.id.ln_bonus)
            imageLikeMain = itemView.findViewById(R.id.image_like_main)
        }
    }
}