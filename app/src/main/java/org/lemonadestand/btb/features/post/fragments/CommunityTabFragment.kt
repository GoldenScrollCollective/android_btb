package org.lemonadestand.btb.features.post.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
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
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chantsune.swipetoaction.views.SimpleSwipeLayout
import com.google.gson.Gson
import org.lemonadestand.btb.R
import org.lemonadestand.btb.activities.AddBonusActivity
import org.lemonadestand.btb.components.LikeMenuView
import org.lemonadestand.btb.components.MediaPreviewView
import org.lemonadestand.btb.components.ReactionsView
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Bonus
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.models.PostsByDate
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.databinding.FragmentCommunityTabBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.setImageUrl
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.dashboard.fragments.MediaPreviewBottomSheetDialog
import org.lemonadestand.btb.features.post.adapter.PostCommentsRecyclerViewAdapter
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Singleton.launchActivity
import org.lemonadestand.btb.utils.Utils

class CommunityTabFragment : BaseFragment(R.layout.fragment_community_tab) {

	private lateinit var mBinding: FragmentCommunityTabBinding
	private lateinit var postsByDateRecyclerViewAdapter: PostsByDateRecyclerViewAdapter
	private lateinit var viewModel: HomeViewModel
	private var shortAnimationDuration: Int = 0
	private var postDateList: ArrayList<PostsByDate> = ArrayList()
	private var tag = "PrivateFragment"
	var clickType = ClickType.COMMON
	var clickedPosition = 0
	var clickedSuperPosition = 0
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentCommunityTabBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)

		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		setUpPublicAdapter()
		setUpViewModel()
		setSwipeRefresh()

		return mBinding.root
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

	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		startLoading()
		val repository = HomeRepository()
		val viewModelProviderFactory = CommonViewModelFactory((context as DashboardActivity).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]
		viewModel.getPosts(page = 0, resource = "", visibility = Singleton.PUBLIC, community = 1, type = "")
		viewModel.postModel.observe(viewLifecycleOwner) {
			stopLoading(true)

			val data = it.data ?: return@observe

			if (data.isNotEmpty()) {
				postDateList.clear()

				val dateList: ArrayList<String> = ArrayList()

				for (i in data.indices) {
					if (!dateList.contains(getDate(data[i].created!!))) {
						dateList.add(getDate(data[i].created!!))
						postDateList.add(
							PostsByDate(
								date = data[i].created,
								posts = data as ArrayList<Post>
							)
						)
					}
				}

				postsByDateRecyclerViewAdapter.values = postDateList
			}
		}


		viewModel.liveError.observe(viewLifecycleOwner) {
			Singleton.handleResponse(response = it, context as Activity, tag)
			ProgressDialogUtil.dismissProgressDialog()
		}

		viewModel.commonResponse.observe(viewLifecycleOwner) {
			handleCommonResponse(context as DashboardActivity, it)
			ProgressDialogUtil.dismissProgressDialog()
			if (it.status == Singleton.SUCCESS) {
				if (clickType == ClickType.DELETE_POST) {
					postDateList[clickedSuperPosition].posts.removeAt(clickedPosition)
					postsByDateRecyclerViewAdapter.values = postDateList

				}
				if (clickType == ClickType.LIKE_POST) {
					if (postDateList[clickedSuperPosition].posts[clickedPosition].meta.like?.size == 0) {
						postDateList[clickedSuperPosition].posts[clickedPosition].meta.like?.add(
							Bonus(by_user = User(), value = "")
						)
						Log.e(
							"sizeLikes=>",
							postDateList[clickedSuperPosition].posts[clickedPosition].meta.like?.size.toString()
						)
						postsByDateRecyclerViewAdapter.values = postDateList
					}
				}
			}
		}


		viewModel.isLoading.observe(viewLifecycleOwner) {
			Log.e("value==>", it.toString())
			if (it) {
				ProgressDialogUtil.showProgressDialog(context as DashboardActivity)
			} else {
				ProgressDialogUtil.dismissProgressDialog()
			}
		}

		viewModel.noInternet.observe(viewLifecycleOwner) {
			Toast.makeText(context, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
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

	private fun stopLoading(isDataAvailable: Boolean) {
		val view = if (isDataAvailable) mBinding.rvPublic else mBinding.noDataView.root
		view.apply {
			alpha = 0f
			visibility = View.VISIBLE

			animate()
				.alpha(1f)
				.setDuration(shortAnimationDuration.toLong())
				.setListener(null)
		}

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
			refreshData()
			mBinding.swipeRefreshLayout.isRefreshing = false
		}
	}

	private fun refreshData() {
		startLoading()
		viewModel.getPosts(visibility = Singleton.PUBLIC, page = 0, community = 1)
	}

	private fun handleLike(post: Post, like: String) {
		viewModel.addLike(LikeBodyModel(metaName = "like", metaValue = like, byUserId = Utils.getData(context, Utils.UID), uniqueId = post.uniqueId))
	}

	private fun handleDelete(post: Post) {
		viewModel.deletePost(post.uniqueId)
	}

	private class PostsByDateRecyclerViewAdapter : BaseRecyclerViewAdapter<PostsByDate>(R.layout.layout_company_posts_item) {
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
					if (getDate(item.posts[i].created!!) == getDate(item.date!!)) {
						tempPostList.add(item.posts[i])
					}
				}
				val adapter = PostsRecyclerViewAdapter(position)
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

	private class PostsRecyclerViewAdapter(val superPosition: Int) : BaseRecyclerViewAdapter<Post>(R.layout.layout_community_posts_item, fullHeight = true) {
		var onPreview: ((value: Post) -> Unit)? = null
		var onLike: ((post: Post, value: String) -> Unit)? = null
		var onDelete: ((value: Post) -> Unit)? = null

		override fun bindView(holder: ViewHolder, item: Post, position: Int) {
			super.bindView(holder, item, position)
			with(holder.itemView) {
				val userImageView = findViewById<ImageView>(R.id.user_image)
				userImageView.setImageUrl(item.user.pictureUrl)

				val titleView = findViewById<TextView>(R.id.titleView)
				titleView.setOnSingleClickListener {
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

					popupWindow.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.darker_gray))) // Transparent color to remove default shadow
					popupWindow.elevation = 20f

					popupWindow.isFocusable = true
					popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

					popupWindow.showAsDropDown(it, 100, 0, 0)
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
					popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
					popupWindow.showAsDropDown(it, 100, 0, 0)
				}

				val lnComment = findViewById<LinearLayout>(R.id.ln_comment)
				lnComment.setOnClickListener {
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
						.setPositiveButton("OK") { _, _ ->
							val message = input.text.toString() // Get the input message
//                    // Handle the confirmation with the input message

							val requestBody = AddCommentBody(
								uniq_id = "",
								resource = "user/${CompanyTabFragment.currentUser!!.uniqueId}",
								html = message,
								created = "",
								parent_id = item.uniqueId,
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
				swipeLayout.setOnSwipeItemClickListener { swipeItem ->

					when (swipeItem.position) {
						0 -> {
							onDelete?.invoke(item)
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

				val lnBonus = findViewById<LinearLayout>(R.id.ln_bonus)
				lnBonus.setOnClickListener {
					val json = Gson().toJson(item)
					(context as Activity).launchActivity<AddBonusActivity> {
						putExtra("reply_data", json)
					}
				}

				val tvComment = findViewById<TextView>(R.id.tv_comment)

				val mediaPreviewView = findViewById<MediaPreviewView>(R.id.mediaPreviewView)
				mediaPreviewView.url = item.media
				mediaPreviewView.setOnClickListener {
					onPreview?.invoke(item)
				}

				val reactionsView = findViewById<ReactionsView>(R.id.reactionsView)
				reactionsView.post = item

				val commentCountView = findViewById<TextView>(R.id.txt_comment)
				val likeCountView = findViewById<TextView>(R.id.txt_like)
				val imageLikeMain = findViewById<ImageView>(R.id.image_like_main)

				val bold = "${item.user.name} shared this story · "
				val small = "${item.createdAgo ?: ""} @${item.organization?.name ?: ""}"
				val total = bold + small
				val spannableStringBuilder = SpannableStringBuilder(total)
				spannableStringBuilder.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_grey)), total.indexOf(small), total.indexOf(small) + small.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
				spannableStringBuilder.setSpan(StyleSpan(Typeface.NORMAL), total.indexOf(small), total.indexOf(small) + small.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
				titleView.text = spannableStringBuilder

				lnBonus.visibility = View.GONE

				tvComment.text = HtmlCompat.fromHtml(item.html, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()

				if (item.replies.isNotEmpty()) {
					commentCountView.text = buildString {
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
					likeCountView.text = buildString {
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

				val commentsRecyclerView = findViewById<RecyclerView>(R.id.commentsRecyclerView)
				val commentsRecyclerViewAdapter = PostCommentsRecyclerViewAdapter(position)   // fixed
				commentsRecyclerViewAdapter.onLike = { post, value -> onLike?.invoke(post, value) }
				commentsRecyclerView.adapter = commentsRecyclerViewAdapter
				commentsRecyclerViewAdapter.values = item.replies
			}
		}
	}

}