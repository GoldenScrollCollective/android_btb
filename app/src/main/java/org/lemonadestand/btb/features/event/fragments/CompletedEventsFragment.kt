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
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.core.models.EventsByDate
import org.lemonadestand.btb.core.repositories.EventRepository
import org.lemonadestand.btb.core.viewModels.EventViewModel
import org.lemonadestand.btb.databinding.FragmentCompletedEventsBinding
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.event.activities.EditRecordActivity
import org.lemonadestand.btb.features.event.adapter.EventsByDateRecyclerViewAdapter
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Singleton.launchActivity
import org.lemonadestand.btb.singleton.Sort
import org.lemonadestand.btb.utils.Utils


class CompletedEventsFragment : Fragment(), OnItemClickListener {

	lateinit var mBinding: FragmentCompletedEventsBinding
	private val resource: UserListModel?
		get() = Utils.getResource(context)


	private lateinit var viewModel: EventViewModel
	private var shortAnimationDuration: Int = 0

	private var tag = "CompletedEventsFragment"
	private var clickType = ClickType.COMMON
	private var clickedPosition = 0
	private var clickedSuperPosition = 0

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentCompletedEventsBinding.inflate(
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
			onSelect = {
				requireActivity().launchActivity<EditRecordActivity> {
					putExtra("event_data", it)
				}
			}
			onDelete = { viewModel.deleteEvent(it.uniqueId) }
		}
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		val repository = EventRepository()
		val viewModelProviderFactory = CommonViewModelFactory((context as DashboardActivity).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]

		viewModel.completedEventsResponse.observe(viewLifecycleOwner) {
			if (it.data.isEmpty()) {
				(mBinding.eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = arrayListOf()
				stopLoading(false)
				return@observe
			}

			val eventsByDates = arrayListOf<EventsByDate>()
			val dateList: ArrayList<String> = ArrayList()

			for (i in 0 until it.data.size) {
				val event = it.data[i]
				val day = event.blessingCompletedDay ?: event.startedDay ?: continue
				if (!dateList.contains(day)) {
					dateList.add(day)
					eventsByDates.add(
						EventsByDate(
							date = event.blessingCompletedAt,
							events = ArrayList(it.data.filter { x -> x.blessingCompletedDay == day || x.startedDay == day })
						)
					)
				}
			}

			(mBinding.eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = eventsByDates
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

	private fun setSwipeRefresh() {
		mBinding.swipeRefreshLayout.setOnRefreshListener {
			refreshData()
			mBinding.swipeRefreshLayout.isRefreshing = false
		}
	}

	fun refreshData() {
		startLoading()
		viewModel.getCompletedEvents(
			ScheduleBody(
				limit = Singleton.API_LIST_LIMIT,
				page = "0",
				sort = Sort.desc.value, //desc //asc
				order_by = "blessing_complete",
				resource = if (!resource?.uniqueId.isNullOrEmpty()) "user/${resource!!.uniqueId}" else "",
				completed = "1",
			)
		)
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

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
		clickedPosition = index
		clickedSuperPosition = superIndex
		clickType = type
		when (type) {
			ClickType.DELETE_POST -> {}
			ClickType.LIKE_POST -> {}
			else -> {

				val eventModel = `object` as Event
				activity?.launchActivity<EditRecordActivity>()
				{
					putExtra("event_data", eventModel)
				}

			}
		}

	}
}