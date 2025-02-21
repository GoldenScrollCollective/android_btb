package org.lemonadestand.btb.features.event.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.components.dialog.DeleteEventDialogFragment
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.manager.EventsManager
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.core.models.EventsPerDate
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.features.event.adapter.EventsByDateRecyclerViewAdapter
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Sort
import org.lemonadestand.btb.utils.Utils


class CompletedEventsFragment : BaseFragment(R.layout.fragment_completed_events) {
	private lateinit var eventsRecyclerView: RecyclerView
	private lateinit var noDataView: RelativeLayout
	private lateinit var shimmerLayout: ShimmerFrameLayout

	private val resource: UserListModel?
		get() = Utils.getResource(context)

	private var shortAnimationDuration: Int = 0

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
			onSelect = { this@CompletedEventsFragment.onSelect?.invoke(it) }
			onDelete = { handleDelete(it) }
		}

		noDataView = rootView.findViewById(R.id.noDataView)
		shimmerLayout = rootView.findViewById(R.id.shimmerLayout)

		setUpViewModel()
	}

	override fun update() {
		super.update()

		refreshData()
	}

	@SuppressLint("NotifyDataSetChanged")
	private fun setUpViewModel() {
		EventsManager.events.observe(viewLifecycleOwner) {
			if (it.isNullOrEmpty()) {
				(eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = arrayListOf()
				stopLoading(false)
				return@observe
			}

			(eventsRecyclerView.adapter as EventsByDateRecyclerViewAdapter).values = EventsPerDate.groupEvents(it)
			stopLoading(true)
		}

		EventsManager.error.observe(viewLifecycleOwner) {
			Singleton.handleResponse(response = it, context as Activity, TAG)
			ProgressDialogUtil.dismissProgressDialog()
		}

		EventsManager.commonResponse.observe(viewLifecycleOwner) {
			handleCommonResponse(context as DashboardActivity, it)
			ProgressDialogUtil.dismissProgressDialog()
		}

		EventsManager.isLoading.observe(viewLifecycleOwner) {
			Log.e("value==>", it.toString())
			if (it) {
				ProgressDialogUtil.showProgressDialog(context as DashboardActivity)
			} else {
				ProgressDialogUtil.dismissProgressDialog()
			}
		}

		EventsManager.noInternet.observe(viewLifecycleOwner) {
			Toast.makeText(context, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
		}
	}

	fun refreshData() {
		startLoading()
		EventsManager.getCompletedEvents(
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
			.setDuration(shortAnimationDuration.toLong())
			.setListener(object : AnimatorListenerAdapter() {
				override fun onAnimationEnd(animation: Animator) {
					shimmerLayout.hide()
				}
			})
	}

	private fun handleDelete(event: Event) {
		val currentUser = Utils.getUser(requireActivity()) ?: return
		val email = currentUser.username ?: return

		DeleteEventDialogFragment(this, event.uniqueId, email).show()
	}
}