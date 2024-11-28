package org.lemonadestand.btb.features.post.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.R
import org.lemonadestand.btb.activities.ShareStoryActivity
import org.lemonadestand.btb.activities.ShowAppreciationActivity
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Bonus
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.models.PostsByDate
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.databinding.FragmentMineBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.post.adapter.PublicAdapter
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Singleton.launchActivity

class MineFragment : Fragment(), OnItemClickListener {
	private lateinit var mBinding: FragmentMineBinding
	private lateinit var publicAdapter: PublicAdapter
	private lateinit var viewModel: HomeViewModel
	private var shortAnimationDuration: Int = 0
	private var postDateList: ArrayList<PostsByDate> = ArrayList()
	private var tag = "MineFragment"
	var clickType = ClickType.COMMON
	var clickedPosition = 0
	var clickedSuperPosition = 0
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentMineBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false

		)
		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		setUpPublicAdapter()
		setUpViewModel()
		setButtonClicks()
		setSwipeRefresh()
		return mBinding.root
	}

	@SuppressLint("InflateParams")
	private fun setButtonClicks() {


		mBinding.btnPublicTab.setOnClickListener {
			val view: View = LayoutInflater.from(context).inflate(R.layout.custom_public_menu, null)
			val popupWindow = PopupWindow(
				view,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT
			)
			popupWindow.isFocusable = true
			popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

			val location = IntArray(2)
			mBinding.btnPublicTab.getLocationOnScreen(location)
			popupWindow.showAtLocation(
				mBinding.btnPublicTab,
				Gravity.NO_GRAVITY,
				location[0],
				location[1] - (popupWindow.height + (mBinding.btnPublicTab.height * 3))
			)
			popupWindow.showAsDropDown(it, 100, 0, 0)
			val tvShareStory = view.findViewById<TextView>(R.id.tv_share_story)
			val tvShowAppreciation = view.findViewById<TextView>(R.id.tv_show_appreciation)
			tvShareStory.setOnClickListener {
				popupWindow.dismiss()
				(context as Activity).launchActivity<ShareStoryActivity>()
			}
			tvShowAppreciation.setOnClickListener {
				popupWindow.dismiss()
				(context as Activity).launchActivity<ShowAppreciationActivity>()
			}
		}
	}

	private fun setUpPublicAdapter() {

		publicAdapter = PublicAdapter(postDateList, requireContext())
		publicAdapter.setOnItemClick(this)
		mBinding.rvPublic.adapter = publicAdapter

	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		startLoading()
		val repository = HomeRepository()
		val viewModelProviderFactory =
			CommonViewModelFactory((context as DashboardActivity).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]
		viewModel.getPostList(visibility = Singleton.MINE, page = 0)
		viewModel.postModel.observe(viewLifecycleOwner) {
			if (!it.data.isNullOrEmpty()) {
				postDateList.clear()

				val dateList: ArrayList<String> = ArrayList()

				for (i in 0 until it.data.size) {
					if (!dateList.contains(getDate(it.data[i].created))) {
						dateList.add(getDate(it.data[i].created))
						postDateList.add(
							PostsByDate(
								date = it.data[i].created,
								posts = it.data as ArrayList<Post>
							)
						)
					}
				}

				publicAdapter.notifyDataSetChanged()
				stopLoading(true)
			} else {
				stopLoading(false)

			}
		}


		viewModel.liveError.observe(viewLifecycleOwner) {
			Singleton.handleResponse(response = it, context as Activity, tag)
			ProgressDialogUtil.dismissProgressDialog()
		}

		viewModel.commonResponse.observe(viewLifecycleOwner) {
			handleCommonResponse(context as DashboardActivity, it)
			ProgressDialogUtil.dismissProgressDialog()
			if (it.status) {
				if (clickType == ClickType.DELETE_POST) {
					postDateList[clickedSuperPosition].posts.removeAt(clickedPosition)
					publicAdapter.updateData(postDateList)

				}
				if (clickType == ClickType.LIKE_POST) {
					if (postDateList[clickedSuperPosition].posts[clickedPosition].meta.like?.size == 0) {
						postDateList[clickedSuperPosition].posts[clickedPosition].meta.like?.add(
							Bonus(by_user = User(), value = "")
						)
						Log.e("sizeLikes=>", postDateList[clickedSuperPosition].posts[clickedPosition].meta.like?.size.toString())
						publicAdapter.updateData(postDateList)
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

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {

		clickedPosition = index
		clickedSuperPosition = superIndex
		clickType = type
		if (type == ClickType.DELETE_POST) {
			val post = `object` as Post
			viewModel.deletePost(post.uniqueId) {
				refreshData()
			}
		} else
			if (type == ClickType.LIKE_POST) {
				val likeResponseModel = `object` as LikeBodyModel
				viewModel.addLike(likeResponseModel)
				Log.e("likeModel==>", likeResponseModel.toString())

			}


	}

	private fun setSwipeRefresh() {
		mBinding.swipeRefreshLayout.setOnRefreshListener {
			refreshData()
			mBinding.swipeRefreshLayout.isRefreshing = false
		}
	}


	private fun refreshData() {
		startLoading()
		viewModel.getPostList(visibility = Singleton.MINE, page = 0)
	}


}