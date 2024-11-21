package org.lemonadestand.btb.features.event.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.core.models.EventsByDate
import org.lemonadestand.btb.core.repositories.EventRepository
import org.lemonadestand.btb.core.viewModels.EventViewModel
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.event.adapter.EventsByDateRecyclerViewAdapter
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.Utils


class ScheduledEventsFragment : BaseFragment(R.layout.fragment_scheduled_events), OnItemClickListener {

	private lateinit var viewModel: EventViewModel
	private var shortAnimationDuration: Int = 0

	private var tag = "ScheduleEventFragment"
	private var clickType = ClickType.COMMON
	private var clickedPosition = 0
	private var clickedSuperPosition = 0

	private lateinit var eventsRecyclerView: RecyclerView
	private lateinit var noDataView: RelativeLayout
	private lateinit var shimmerLayout: ShimmerFrameLayout

	private val resource: UserListModel?
		get() = Utils.getResource(context)

	var onSelect: ((value: Event?) -> Unit)? = null

	override fun init() {
		super.init()

		shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

		val swipeRefreshLayout = rootView.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
		swipeRefreshLayout.setOnRefreshListener {
			refreshData()
			swipeRefreshLayout.isRefreshing = false
		}

		eventsRecyclerView = rootView.findViewById(R.id.eventsRecyclerView)
		eventsRecyclerView.adapter = EventsByDateRecyclerViewAdapter().apply {
			onSelect = { this@ScheduledEventsFragment.onSelect?.invoke(it) }
		}

		noDataView = rootView.findViewById(R.id.noDataView)
		shimmerLayout = rootView.findViewById(R.id.shimmerLayout)

		setUpViewModel()

		refreshData()
	}

	fun refreshData() {
		startLoading()
		viewModel.getScheduledEvents(
			ScheduleBody(
				limit = Singleton.API_LIST_LIMIT,
				page = "0",
				sort = "asc", //desc //asc
				order_by = "start",
				resource = if (!resource?.uniqueId.isNullOrEmpty()) "user/${resource!!.uniqueId}" else "",
				completed = "0",
			)
		)
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		startLoading()
		val repository = EventRepository()
		val viewModelProviderFactory =
			CommonViewModelFactory((context as DashboardActivity).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]

		viewModel.scheduledEventsResponse.observe(viewLifecycleOwner) {
			if (it.data.isEmpty()) {
				(eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = arrayListOf()
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

			(eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = eventsByDates
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
		eventsRecyclerView.hide()
		noDataView.hide()
		shimmerLayout.apply {
			alpha = 0f
			visibility = View.VISIBLE
			animate()
				.alpha(1f)
				.setDuration(0)
				.setListener(null)
		}
		shimmerLayout.startShimmer()
	}

	private fun stopLoading(isDataAvailable: Boolean) {
		eventsRecyclerView.hide()


		val view = if (isDataAvailable) eventsRecyclerView else noDataView
		view.apply {
			alpha = 0f
			visibility = View.VISIBLE
			animate()
				.alpha(1f)
				.setDuration(shortAnimationDuration.toLong())
				.setListener(null)
		}

		shimmerLayout.animate()
			.alpha(0f)
			.setDuration(650)
			.setListener(object : AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					shimmerLayout.hide()
				}
			})
	}

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {

		clickedPosition = index
		clickedSuperPosition = superIndex
		clickType = type
		when (type) {
			ClickType.DELETE_POST -> {

			}

			ClickType.LIKE_POST -> {


			}

			else -> {
				val eventModel = `object` as Event
				onSelect?.invoke(eventModel)
			}
		}
	}
}