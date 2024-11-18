package org.lemonadestand.btb.features.common.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseBottomSheetDialogFragment
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.databinding.FragmentSelectMultiUsersBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.UserRepository
import org.lemonadestand.btb.mvvm.viewmodel.UserViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Sort
import java.util.Locale


class SelectMultiUsersBottomSheetFragment : BaseBottomSheetDialogFragment(R.layout.fragment_select_multi_users) {

	lateinit var mBinding: FragmentSelectMultiUsersBinding

	private var shortAnimationDuration: Int = 0
	private var tag: String = "UserListFragment"
	private lateinit var viewModel: UserViewModel

	private var users = ArrayList<UserListModel>()
		set(value) {
			field = value
			search()
		}
	private var searchResults = arrayListOf<UserListModel>()
		set(value) {
			field = value
			(mBinding.rvUserList.adapter as UsersRecyclerViewAdapter).values = value
		}
	private lateinit var callback: OnItemClickListener

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentSelectMultiUsersBinding.inflate(
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

		loadData()
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

		if (!listFromGson.isNullOrEmpty()) {
			for (i in 0 until users.size) {
				for (j in 0 until listFromGson.size) {
					if (users[i].username == listFromGson[j].username) {
						users[i].isSelected = true
					}
				}
			}

		}
	}

	private fun startLoading() {
		mBinding.shimmerLayout.startShimmer()
		mBinding.rvUserList.visibility = View.GONE
		mBinding.noDataView.hide()
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		val repository = UserRepository()
		val viewModelProviderFactory = CommonViewModelFactory((requireActivity()).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[UserViewModel::class.java]
		viewModel.userResponseModel.observe(viewLifecycleOwner) {
			stopLoading(true)

			if (it.data.isNullOrEmpty()) {
				users = ArrayList()
			} else {
				users = ArrayList(it.data)
				mBinding.rvUserList
			}

			(mBinding.rvUserList.adapter as UsersRecyclerViewAdapter).values = users
			handleData()
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
		mBinding.tvDone.setOnClickListener {
			dismiss()

			callback.onItemClicked(`object` = (mBinding.rvUserList.adapter as UsersRecyclerViewAdapter).getSelectedUsers(), index = 0)

			dismiss()
		}
	}

	private fun setAdapter() {
		mBinding.shimmerLayout.startShimmer()

		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		val usersRecyclerViewAdapter = UsersRecyclerViewAdapter()
		usersRecyclerViewAdapter.setOnItemClick(callback)
		mBinding.rvUserList.adapter = usersRecyclerViewAdapter
	}

	private fun loadData(query: String = "") {
		startLoading()
		viewModel.getUserList(page = 0, sort = Sort.asc, orderBy = "name", query = query)
	}

	private fun search() {
		val query = mBinding.searchView.text.toString()
		if (query.isNullOrEmpty()) {
			searchResults = ArrayList(users)
			return
		}
		searchResults = ArrayList(users.filter { it.name.contains(query.lowercase(Locale.getDefault())) })
	}

	private fun setSearch() {
		mBinding.searchView.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {
				search()
			}

			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
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

	fun setCallback(callback: OnItemClickListener) {
		this.callback = callback
	}

	private class UsersRecyclerViewAdapter : BaseRecyclerViewAdapter<UserListModel>(R.layout.layout_select_multi_users_item) {
		private var onItemClickListener: OnItemClickListener? = null
		fun setOnItemClick(onItemClickListener: OnItemClickListener) {
			this.onItemClickListener = onItemClickListener
		}

		override fun bindView(holder: ViewHolder, item: UserListModel, position: Int) {
			super.bindView(holder, item, position)
			with(holder.itemView) {
				val userNameView = findViewById<TextView>(R.id.tv_user_name)
				userNameView.text = item.name

				val checkmarkView = findViewById<ImageView>(R.id.ic_check)
				checkmarkView.visibility = if (item.isSelected) View.VISIBLE else View.GONE

				setOnSingleClickListener {
					values!![position].isSelected = !values!![position].isSelected
					notifyDataSetChanged()
				}
			}
		}

		fun getSelectedUsers(): ArrayList<UserListModel> {
			return ArrayList(values?.filter { it.isSelected } ?: arrayListOf())
		}
	}
}