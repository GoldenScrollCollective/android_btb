package org.lemonadestand.btb.features.interest.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.databinding.FragmentContactBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.UserRepository
import org.lemonadestand.btb.mvvm.viewmodel.UserViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.Utils

class SelectContactFragment : Fragment() {
	lateinit var mBinding: FragmentContactBinding

	private var shortAnimationDuration: Int = 0
	private var tag: String = "TeamFragment"
	private lateinit var viewModel: UserViewModel
	private var userList = ArrayList<UserListModel>()
	private var userTemp = ArrayList<UserListModel>()
	private lateinit var callback: OnItemClickListener

	private var isEvent: Boolean = false

	var onSelect: ((value: UserListModel) -> Unit)? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentContactBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)
		getData()
		setAdapter()
		setUpViewModel()

		return mBinding.root

	}

	private fun getData() {
		val args = arguments
		isEvent = args!!.getBoolean("is_event", false)
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
		viewModel.getContactList(page = 0)
		viewModel.contactResponseModel.observe(viewLifecycleOwner) {
			if (!it.data.isNullOrEmpty()) {
				userList.clear()
				userTemp.clear()
				userList.addAll(it.data)
				userTemp.addAll(it.data)
				val selection: UserListModel? = if (isEvent) {
					userList.firstOrNull { its -> its.uniqueId == Utils.getUserIdEvent(context) }
				} else {
					userList.firstOrNull { its -> its.uniqueId == Utils.getUserIdInterest(context) }
				}
				if (selection != null) {
					selection.isSelected = true
				}
				mBinding.rvUserList.adapter!!.notifyDataSetChanged()
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

	private fun setAdapter() {
		mBinding.shimmerLayout.startShimmer()
		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		val contactsRecyclerViewAdapter = ContactsRecyclerViewAdapter(isEvent)
		mBinding.rvUserList.adapter = contactsRecyclerViewAdapter
		contactsRecyclerViewAdapter.onItemClick = { onSelect?.invoke(it) }
		contactsRecyclerViewAdapter.values = userList
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
		Log.e("here=>", "done")
		this.callback = callback

	}

	private class ContactsRecyclerViewAdapter(val isEvent: Boolean) : BaseRecyclerViewAdapter<UserListModel>(R.layout.row_team_list) {
		override fun bindView(holder: ViewHolder, item: UserListModel, position: Int) {
			super.bindView(holder, item, position)

			with(holder.itemView) {
				val userNameView = findViewById<TextView>(R.id.tv_user_name)
				userNameView.text = item.name

				val checkmarkView = findViewById<ImageView>(R.id.ic_check)
				checkmarkView.visibility = if (item.isSelected) View.VISIBLE else View.GONE

				setOnSingleClickListener {
					if (isEvent) {
						Utils.saveUserIDEvent(context, item.uniqueId)
						Utils.saveUserModelEvent(context, item)
					} else {
						Utils.saveUserIDInterest(context, item.uniqueId)
						Utils.saveUserModelInterest(context, item)
					}

					onItemClick?.invoke(item)
				}
			}
		}
	}


}