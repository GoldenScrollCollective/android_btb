package org.lemonadestand.btb.features.post.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bumptech.glide.Glide
import com.github.chantsune.swipetoaction.views.SimpleSwipeLayout
import com.github.chantsune.swipetoaction.views.SwipeLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.activities.AddBonusActivity
import org.lemonadestand.btb.components.LikeMenuView
import org.lemonadestand.btb.components.MediaPreviewView
import org.lemonadestand.btb.components.ReactionsView
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.components.dialog.DeletePostDialogFragment
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.manager.PostsManager
import org.lemonadestand.btb.core.models.Bonus
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.models.PostsByDate
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.databinding.FragmentCompanyTabBinding
import org.lemonadestand.btb.extensions.CustomTypefaceSpan
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.dashboard.fragments.MediaPreviewBottomSheetDialog
import org.lemonadestand.btb.features.post.adapter.PostCommentsRecyclerViewAdapter
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.Utils


class CompanyTabFragment : BaseFragment(R.layout.fragment_company_tab) {
	private lateinit var mBinding: FragmentCompanyTabBinding
	private lateinit var postsByDateRecyclerViewAdapter: PostsByDateRecyclerViewAdapter
	private var shortAnimationDuration: Int = 0
	private var postDateList: ArrayList<PostsByDate> = ArrayList()
	private var tag = "PublicFragment"
	private var clickType = ClickType.COMMON
	private var clickedPosition = 0
	private var clickedSuperPosition = 0
	private var page = 1
	private var isLoading = false

	var visibility: Post.Visibility = Post.Visibility.PUBLIC
		set(value) {
			field = value
			reloadPosts()
		}

	companion object {
		var currentUser: User? = null
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		super.onCreateView(inflater, container, savedInstanceState)

		mBinding = FragmentCompanyTabBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)

		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		setUpPublicAdapter()
		setUpViewModel()

		currentUser = Utils.getUser(requireActivity())

		setSwipeRefresh()

		return mBinding.root
	}

	override fun update() {
		super.update()

		reloadPosts()
	}

	private fun setUpPublicAdapter() {
		postsByDateRecyclerViewAdapter = PostsByDateRecyclerViewAdapter()
		postsByDateRecyclerViewAdapter.onPreview = { post ->
			val previewBottomSheetDialog = MediaPreviewBottomSheetDialog(post)
			previewBottomSheetDialog.show(childFragmentManager)
		}
		postsByDateRecyclerViewAdapter.onLike = { post, value ->
			handleLike(post, value)
		}
		postsByDateRecyclerViewAdapter.onDelete = { post ->
			handleDelete(post)
		}
		mBinding.rvPublic.adapter = postsByDateRecyclerViewAdapter
		mBinding.rvPublic.addOnScrollListener(object : OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)

				if (isLoading || mBinding.rvPublic.layoutManager !is LinearLayoutManager) {
					return
				}

				val layoutManager = recyclerView.layoutManager as LinearLayoutManager
				val visibleItemCount = layoutManager.childCount
				val totalItemCount = layoutManager.itemCount
				val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
				if (totalItemCount <= (firstVisibleItemPosition + visibleItemCount)) {
					loadMorePosts()
				}
			}
		})
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		PostsManager.noInternet.observe(viewLifecycleOwner) {
			Toast.makeText(context, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
		}
		PostsManager.isLoading.observe(viewLifecycleOwner) {
			Log.e("value==>", it.toString())
			if (it) {
				ProgressDialogUtil.showProgressDialog(context as DashboardActivity)
			} else {
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
		PostsManager.error.observe(viewLifecycleOwner) {
			Singleton.handleResponse(response = it, context as Activity, tag)
			ProgressDialogUtil.dismissProgressDialog()
		}
		PostsManager.commonResponse.observe(viewLifecycleOwner) {
			handleCommonResponse(context as DashboardActivity, it)
			ProgressDialogUtil.dismissProgressDialog()
			if (it.status) {
				if (clickType == ClickType.DELETE_POST) {
					postDateList[clickedSuperPosition].posts.removeAt(clickedPosition)
					postsByDateRecyclerViewAdapter.notifyDataSetChanged()
				}
				if (clickType == ClickType.LIKE_POST) {
					if (postDateList[clickedSuperPosition].posts[clickedPosition].meta.like.size == 0) {
						postDateList[clickedSuperPosition].posts[clickedPosition].meta.like.add(
							Bonus(by_user = User(), value = "")
						)
						Log.e(
							"sizeLikes=>",
							postDateList[clickedSuperPosition].posts[clickedPosition].meta.like.size.toString()
						)
						postsByDateRecyclerViewAdapter.values = postDateList
					}
				}
			}
		}
	}

	private fun handlePosts() {
		val posts = arrayListOf<Post>()
		posts.addAll(PostsManager.sharedPosts.value ?: arrayListOf())
		posts.addAll(PostsManager.posts.value ?: arrayListOf())

		val isDataAvailable = posts.isNotEmpty()
		if (isDataAvailable) {
			postDateList.clear()

			val dateList: ArrayList<String> = ArrayList()

			for (i in 0 until posts.size) {
				if (!dateList.contains(getDate(posts[i].created))) {
					dateList.add(getDate(posts[i].created))
					postDateList.add(
						PostsByDate(
							date = posts[i].created,
							posts = posts
						)
					)
				}
			}
			postsByDateRecyclerViewAdapter.values = postDateList
		}

		if (page <= 1) {
			val view = if (isDataAvailable) mBinding.rvPublic else mBinding.noDataView.root
			view.apply {
				alpha = 0f
				visibility = View.VISIBLE

				animate()
					.alpha(1f)
					.setDuration(shortAnimationDuration.toLong())
					.setListener(null)
			}
		}
	}

	private fun startLoading() {
		mBinding.shimmerLayout.startShimmer()
		mBinding.rvPublic.hide()
		mBinding.noDataView.root.hide()
		mBinding.shimmerLayout.apply {
			alpha = 0f
			visibility = View.VISIBLE
			animate()
				.alpha(1f)
				.setDuration(0)
				.setListener(null)
		}
		mBinding.shimmerLayout.startShimmer()
	}

	private fun stopLoading() {
		mBinding.shimmerLayout.animate()
			.alpha(0f)
			.setDuration(650)
			.setListener(object : AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					mBinding.shimmerLayout.hide()
				}
			})
	}

	private fun setSwipeRefresh() {
		mBinding.swipeRefreshLayout.setOnRefreshListener {
			reloadPosts()
			PostsManager.resetSharedStories()
			mBinding.swipeRefreshLayout.isRefreshing = false
		}

		mBinding.rvPublic.addOnScrollListener(object : OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				super.onScrollStateChanged(recyclerView, newState)
				// Disable SwipeRefreshLayout's ability to intercept touch events during scrolling
				mBinding.swipeRefreshLayout.isEnabled = newState == RecyclerView.SCROLL_STATE_IDLE
			}
		})
	}

	private fun loadPosts(showLoading: Boolean) {
		if (showLoading) {
			startLoading()
		}
		isLoading = true
		PostsManager.getPosts(
			page = page,
			resource = "",
			visibility = visibility,
			community = 0,
			callback = {
				isLoading = false
				if (showLoading) {
					stopLoading()
				}
				handlePosts()
			})
	}

	private fun loadMorePosts() {
		page += 1
		loadPosts(false)
	}

	private fun reloadPosts() {
		page = 1
		loadPosts(true)
	}

	private fun handleLike(post: Post, like: String) {
		PostsManager.addLike(
			LikeBodyModel(
				metaName = "like",
				metaValue = like,
				byUserId = Utils.getData(context, Utils.UID),
				uniqueId = post.uniqueId
			)
		)
	}

	private fun handleDelete(post: Post) {
		val email = currentUser?.username ?: return
		DeletePostDialogFragment(this, post.uniqueId, email, {
			reloadPosts()
		}).show()
	}

	private class PostsByDateRecyclerViewAdapter :
		BaseRecyclerViewAdapter<PostsByDate>(R.layout.layout_company_posts_by_date_item) {
		var onPreview: ((value: Post) -> Unit)? = null
		var onLike: ((post: Post, value: String) -> Unit)? = null
		var onDelete: ((value: Post) -> Unit)? = null

		override fun bindView(holder: ViewHolder, item: PostsByDate, position: Int) {
			super.bindView(holder, item, position)

			with(holder.itemView) {
				val tvDate = findViewById<TextView>(R.id.tv_date)
				tvDate.text = getDate(item.date!!)

				val tempPostList: ArrayList<Post> = ArrayList()

				for (i in 0 until item.posts.size) {
					if (getDate(item.posts[i].created) == getDate(item.date!!)) {
						tempPostList.add(item.posts[i])
					}
				}
				val adapter = PostsRecyclerViewAdapter()
				adapter.onPreview = { post -> onPreview?.invoke(post) }
				adapter.onLike = { post, value -> onLike?.invoke(post, value) }
				adapter.onDelete = { post -> onDelete?.invoke(post) }

				val recyclerView = findViewById<RecyclerView>(R.id.rv_public_sub)
				recyclerView.setHasFixedSize(false)
				recyclerView.layoutManager = LinearLayoutManager(context)
				recyclerView.adapter = adapter
				adapter.values = tempPostList
			}
		}
	}

	private class PostsRecyclerViewAdapter : BaseRecyclerViewAdapter<Post>(R.layout.layout_company_posts_item, fullHeight = true) {
		var onPreview: ((value: Post) -> Unit)? = null
		var onLike: ((post: Post, value: String) -> Unit)? = null
		var onDelete: ((value: Post) -> Unit)? = null

		override fun bindView(holder: ViewHolder, item: Post, position: Int) {
			super.bindView(holder, item, position)
			with(holder.itemView) {
				val userImageView = findViewById<ImageView>(R.id.user_image)
				Glide.with(context).load(item.user.pictureUrl).into(userImageView)

				val byUserImageContainer = findViewById<CardView>(R.id.cd_sub_name)
				val byUserImageView = findViewById<ImageView>(R.id.by_user_image)
				if (!item.type.isNullOrEmpty()) {
					Glide.with(context).load(item.byUser?.pictureUrl).into(byUserImageView)
				} else {
					byUserImageContainer.visibility = View.GONE
				}

				val mediumFont = ResourcesCompat.getFont(context, R.font.urbanist_medium)
				val boldFont = ResourcesCompat.getFont(context, R.font.urbanist_bold)
				val blackFont = ResourcesCompat.getFont(context, R.font.urbanist_black)

				val titleView = findViewById<TextView>(R.id.titleView)
				if (item.type.isNullOrBlank()) {
					item.byUser?.let { resource ->
						val title = if (item.anonymous == "1") "Story shared anonymously" else "${resource.name} shared this story"
						val dot = " · "
						val timestamp = item.createdAgo ?: ""
						titleView.text = SpannableStringBuilder("$title$dot$timestamp").apply {
							setSpan(AbsoluteSizeSpan(16, true), 0, title.length + dot.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
							setSpan(CustomTypefaceSpan(boldFont), 0, title.length + dot.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

							setSpan(AbsoluteSizeSpan(12, true), title.length + dot.length, title.length + dot.length + timestamp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
							setSpan(CustomTypefaceSpan(mediumFont), title.length + dot.length, title.length + dot.length + timestamp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
						}
					}

				} else {
					val username = item.user.name
					val plus = if (item.users.size > 1) " +1" else ""
					val txtFor = " for "
					val title = "$username$plus$txtFor${item.title}"
					titleView.text = SpannableStringBuilder(title).apply {
						setSpan(AbsoluteSizeSpan(16, true), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

						setSpan(CustomTypefaceSpan(boldFont), 0, username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
						setSpan(CustomTypefaceSpan(blackFont), username.length, username.length + plus.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
						setSpan(CustomTypefaceSpan(boldFont), username.length + plus.length + txtFor.length, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
					}
				}
				titleView.setOnClickListener {
					val cardView = CardView(context)
					cardView.setPadding(5, 5, 5, 5)

					val layout2 = LinearLayout(context)
					layout2.orientation = LinearLayout.VERTICAL

					for (i in 0 until item.users.size) {

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

						Glide.with(context).load(item.users[i].picture).into(image)

						val messagearea = TextView(context)
						messagearea.text = item.users[i].name
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

					popupWindow.setBackgroundDrawable(
						ContextCompat.getColor(
							context,
							android.R.color.darker_gray
						).toDrawable()
					) // Transparent color to remove default shadow
					popupWindow.elevation = 20f

					popupWindow.isFocusable = true
					popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

					popupWindow.showAsDropDown(it, 100, 0, 0)
				}

				val descriptionView = findViewById<TextView>(R.id.descriptionView)
				if (!item.type.isNullOrBlank()) {
					item.byUser?.let { byUser ->
						val txtBy = "by "
						val username = byUser.name
						val ago = " · ${item.createdAgo ?: ""}"
						val title = "$txtBy$username$ago"
						descriptionView.text = SpannableStringBuilder("$txtBy$username$ago").apply {
							setSpan(AbsoluteSizeSpan(14, true), 0, txtBy.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
							setSpan(CustomTypefaceSpan(mediumFont), 0, txtBy.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

							setSpan(AbsoluteSizeSpan(14, true), txtBy.length, txtBy.length + username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
							setSpan(CustomTypefaceSpan(boldFont), txtBy.length, txtBy.length + username.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

							setSpan(AbsoluteSizeSpan(12, true), txtBy.length + username.length, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
							setSpan(CustomTypefaceSpan(mediumFont), txtBy.length + username.length, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
						}
					}
				} else {
					descriptionView.visibility = View.GONE
				}

				val lnLike = findViewById<LinearLayout>(R.id.ln_like)
				lnLike.setOnClickListener {

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
					popupWindow.showAsDropDown(it, 100, 0, 0)
				}

				val btnComment = findViewById<LinearLayout>(R.id.btnComment)
				btnComment.setOnClickListener {
					// add to set alert dialog's styls
					val title = TextView(context) //custom title
					title.text = "Add Comment"
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
						.setCustomTitle(title)
						.setView(layout) // Set the EditText as view in the dialog
						.setPositiveButton("Post") { _, _ ->
							val message = input.text.toString() // Get the input message
//                    // Handle the confirmation with the input message

							val requestBody = AddCommentBody(
								uniq_id = "",
								resource = "user/${currentUser!!.uniqueId}",
								html = message,
								created = "",
								parent_id = item.uniqueId,
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

				val swipeLayout = findViewById<SimpleSwipeLayout>(R.id.swipe_layout)

				val btnAddBonus = findViewById<LinearLayout>(R.id.btnAddBonus)
				btnAddBonus.visibility = if (item.type != null) View.VISIBLE else View.GONE
				btnAddBonus.setOnClickListener {
					val post = item.copy(
						resource = "user/${currentUser!!.uniqueId}",
						type = "reply",
						parentId = item.uniqueId
					)

					val intent = Intent(context as Activity, AddBonusActivity::class.java)
					intent.putExtra("post", post)
					context.startActivity(intent)
				}

				val tvComment = findViewById<TextView>(R.id.tv_comment)

				val mediaPreviewView = findViewById<MediaPreviewView>(R.id.mediaPreviewView)
				mediaPreviewView.url = item.media
				mediaPreviewView.setOnClickListener {
					onPreview?.invoke(item)
				}

				val reactionsView = findViewById<ReactionsView>(R.id.reactionsView)
				reactionsView.post = item

				val txtCommentCount = findViewById<TextView>(R.id.txt_comment)
				val txtLikeCount = findViewById<TextView>(R.id.txt_like)
				val imageLikeMain = findViewById<ImageView>(R.id.image_like_main)

				tvComment.text =
					HtmlCompat.fromHtml(item.html, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()

				if (item.replies.isNotEmpty()) {
					txtCommentCount.text = buildString {
						append("Comment")
//                append("(")
//                append(data.replies.size)
//                append(")")
					}

					var commentStr = ""
					if (item.replies.size == 1) commentStr = "1 Comment"
					if (item.replies.size > 1) commentStr = "${item.replies.size} Comments"
				}

				if (!item.meta.like.isNullOrEmpty()) {
					txtLikeCount.text = buildString {
						append("Like")
//                append("(")
//                append(data.meta.like.size)
//                append(")")
					}

					if (item.meta.like.size != 0) {
						imageLikeMain.setImageResource(R.drawable.ic_like_up)
					}
				}


				/* holder.tv_title.setText(data.getName());
				holder.tv_desc.setText(data.getDesc());

				Glide.with(context)
						.load(data.getImage())
						.into(holder.iv_image);*/

				swipeLayout.setOnSwipeItemClickListener { swipeItem ->

					when (swipeItem.position) {
						0 -> {
							onDelete?.invoke(item)
							swipeLayout.setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, true)
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

				val commentsRecyclerView = findViewById<RecyclerView>(R.id.commentsRecyclerView)
				val commentsRecyclerViewAdapter = PostCommentsRecyclerViewAdapter()   // fixed
				commentsRecyclerViewAdapter.onLike = { post, value -> onLike?.invoke(post, value) }
				commentsRecyclerView.adapter = commentsRecyclerViewAdapter
				commentsRecyclerViewAdapter.values = item.replies
			}
		}
	}
}