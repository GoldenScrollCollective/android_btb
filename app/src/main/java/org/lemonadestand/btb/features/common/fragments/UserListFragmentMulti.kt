package org.lemonadestand.btb.features.common.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseBottomSheetDialogFragment
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.common.adapter.UserListAdapterMulti
import org.lemonadestand.btb.databinding.FragmentUserListMultiBinding
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.UserRepository
import org.lemonadestand.btb.mvvm.viewmodel.UserViewModel
import org.lemonadestand.btb.singleton.Singleton
import java.util.Locale


class UserListFragmentMulti() : BaseBottomSheetDialogFragment(R.layout.fragment_user_list_multi) {

	lateinit var mBinding: FragmentUserListMultiBinding
	private lateinit var userListAdapter: UserListAdapterMulti
	private var shortAnimationDuration: Int = 0
	private var tag: String = "UserListFragment"
	private lateinit var viewModel: UserViewModel
	private var userList = ArrayList<UserListModel>()
	private var userTemp = ArrayList<UserListModel>()
	private lateinit var callback: OnItemClickListener

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentUserListMultiBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)

		return mBinding.root
	}

	override fun init() {
		super.init()

		setAdapter()
		setOnClicks()
		setUpViewModel()
		setSearch()

		val bundle = arguments ?: return
		if (bundle.containsKey("title")) {
			val title = bundle.getString("title")
			mBinding.titleView.text = title
		}
	}

	private fun handleData() {
		val bundle = arguments ?: return

		//val b = bundle.getBundle("list")
		val jsonList = bundle.getString("list")

		val gson = Gson()
		val listFromGson = gson.fromJson<ArrayList<UserListModel>>(
			jsonList,
			object : TypeToken<ArrayList<UserListModel>>() {}.type
		)


		Log.e("Multi selector data=>", listFromGson.toString())

		if (!listFromGson.isNullOrEmpty())
		{
			Log.e("userList=>",userList.size.toString())
			Log.e("listFromGson=>",listFromGson.size.toString())
			for (i in 0 until  userList.size)
			{
				for (j in 0 until  listFromGson.size)
				{
					if (userList[i].username == listFromGson[j].username)
					{
						userList[i].isSelected = true
					}
				}
			}

		}
	}

	private fun startLoading() {
		mBinding.shimmerLayout.startShimmer()
		mBinding.rvUserList.hide()
		mBinding.noDataView.hide()
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		startLoading()
		val repository = UserRepository()
		val viewModelProviderFactory =
			CommonViewModelFactory((requireActivity()).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]
		viewModel.getUserList(page = 0)
		viewModel.userResponseModel.observe(viewLifecycleOwner) {
			if (!it.data.isNullOrEmpty()) {
				userList.clear()
				userTemp.clear()
				userList.addAll(it.data)
				userTemp.addAll(it.data)
				handleData()
				userListAdapter.notifyDataSetChanged()
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
		}

		viewModel.isLoading.observe(viewLifecycleOwner) {
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

	private fun setOnClicks() {
		mBinding.tvDone.setOnClickListener { dismiss()

			callback.onItemClicked(`object` = userListAdapter.getSelectedUserList(), index = 0)

			dismiss()
		}
	}

	private fun setAdapter() {
		mBinding.shimmerLayout.startShimmer()
		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
		userListAdapter = UserListAdapterMulti(userList, context = requireContext())
		userListAdapter.setOnItemClick(callback)
		mBinding.rvUserList.adapter = userListAdapter
	}

	private fun setSearch() {
		mBinding.searchView.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {

				var tempList: ArrayList<UserListModel> = ArrayList()
				val query = s.toString().lowercase(Locale.getDefault())
				if (query.isNotEmpty()) {
					Log.e("dwdhwd", "wdwdwd")
					Log.e("query=>", query)
					tempList = userList.filter {
						it.name.contains(
							query,
							ignoreCase = true
						)
					} as ArrayList<UserListModel>
					Log.e("tempList=>", tempList.toString())
					userList.clear()
					userList.addAll(tempList)

					userListAdapter.notifyDataSetChanged()
				} else {
					userList.clear()
					userList.addAll(userTemp)
					userListAdapter.notifyDataSetChanged()
				}

			}

			override fun beforeTextChanged(
				s: CharSequence, start: Int,
				count: Int, after: Int
			) {
			}

			override fun onTextChanged(
				s: CharSequence, start: Int,
				before: Int, count: Int
			) {

			}
		})
	}

	private fun stopLoading(isDataAvailable: Boolean) {
		val view = if (isDataAvailable) mBinding.rvUserList else mBinding.noDataView
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

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
		bottomSheetDialog.setOnShowListener { dialog: DialogInterface ->

		}
		bottomSheetDialog.apply {
			// behavior.state = BottomSheetBehavior.STATE_EXPANDED
			//behavior.peekHeight = //your harcoded or dimen height
		}

		return bottomSheetDialog
	}

	fun setCallback(callback: OnItemClickListener) {
		this.callback = callback
	}


}