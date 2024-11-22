package org.lemonadestand.btb.features.event.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.core.models.EventsPerDate
import org.lemonadestand.btb.core.repositories.EventRepository
import org.lemonadestand.btb.core.viewModels.EventViewModel
import org.lemonadestand.btb.databinding.FragmentPastEventBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.event.adapter.EventsByDateRecyclerViewAdapter
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Sort
import org.lemonadestand.btb.utils.Utils


class PastEventFragment : Fragment() {

	lateinit var mBinding: FragmentPastEventBinding
	private val resource: UserListModel?
		get() = Utils.getResource(context)

	private lateinit var viewModel: EventViewModel
	private var shortAnimationDuration: Int = 0

	private var tag = "PastEventFragment"

	var onSelect: ((value: Event) -> Unit)? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentPastEventBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)
		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		setUpPublicAdapter()
		setUpViewModel()
		setSwipeRefresh()

		refreshData()

		return mBinding.root
	}

	private fun setUpPublicAdapter() {
		mBinding.eventsRecyclerView.adapter = EventsByDateRecyclerViewAdapter().apply {
			onSelect = { this@PastEventFragment.onSelect?.invoke(it) }
			onDelete = { viewModel.deletePastEvent(it.uniqueId) }
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		val repository = EventRepository()
		val viewModelProviderFactory = CommonViewModelFactory((context as DashboardActivity).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]

		viewModel.pastEventsResponse.observe(viewLifecycleOwner) {
			if (it.data.isNullOrEmpty()) {
				(mBinding.eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = arrayListOf()
				stopLoading(false)
				return@observe
			}

			(mBinding.eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = EventsPerDate.groupEvents(it.data)
			stopLoading(true)
		}

		viewModel.error.observe(viewLifecycleOwner) {
			Singleton.handleResponse(response = it, context as Activity, tag)
			ProgressDialogUtil.dismissProgressDialog()
		}

		viewModel.commonResponse.observe(viewLifecycleOwner) {
			handleCommonResponse(context as DashboardActivity, it)
			ProgressDialogUtil.dismissProgressDialog()
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
		mBinding.eventsRecyclerView.hide()
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

	private fun setSwipeRefresh() {
		mBinding.swipeRefreshLayout.setOnRefreshListener {
			refreshData()
			mBinding.swipeRefreshLayout.isRefreshing = false
		}
	}

	fun refreshData() {
		startLoading()
		viewModel.getPastEvents(
			PastEventBody(
				limit = Singleton.API_LIST_LIMIT,
				page = "0",
				sort = Sort.desc.value, //desc //asc
				order_by = "start",
				resource = if (!resource?.uniqueId.isNullOrEmpty()) "user/${resource!!.uniqueId}" else "",
				completed = "0",
				archive = "1"
			)
		)
	}

	private fun stopLoading(isDataAvailable: Boolean) {
		val view = if (isDataAvailable) mBinding.eventsRecyclerView else mBinding.noDataView.root
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
}