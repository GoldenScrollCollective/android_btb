package org.lemonadestand.btb.features.post.fragments

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import org.lemonadestand.btb.App
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.NavHeaderView
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.components.base.BaseRecyclerViewAdapter
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.models.Member
import org.lemonadestand.btb.core.repositories.TeamRepository
import org.lemonadestand.btb.core.viewModels.TeamViewModel
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.extensions.show
import org.lemonadestand.btb.features.dashboard.activities.DashboardActivity
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.singleton.Singleton

class TeamsFragment : BaseFragment(R.layout.fragment_teams) {
	private var TAG: String = "TeamsFragment"

	private lateinit var swipeRefresh: SwipeRefreshLayout
	private lateinit var shimmerLayout: ShimmerFrameLayout
	private lateinit var recyclerView: RecyclerView
	private lateinit var noDataView: RelativeLayout

	private lateinit var repository: TeamRepository
	private lateinit var viewModel: TeamViewModel

	override fun init() {
		super.init()

		val navHeaderView = rootView.findViewById<NavHeaderView>(R.id.navHeaderView)
		navHeaderView.onLeftPressed = { navController.navigateUp() }

		swipeRefresh = rootView.findViewById(R.id.swipeRefresh)
		swipeRefresh.setOnRefreshListener {
			load()
		}
		shimmerLayout = rootView.findViewById(R.id.shimmerLayout)
		recyclerView = rootView.findViewById(R.id.recyclerView)
		recyclerView.adapter = TeamRecyclerViewAdapter()
		noDataView = rootView.findViewById(R.id.noDataView)

		repository = TeamRepository()
		val viewModelProviderFactory = CommonViewModelFactory(App.instance, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[TeamViewModel::class.java]
		viewModel.response.observe(viewLifecycleOwner) { response ->
			val data = response.data
			(recyclerView.adapter as TeamRecyclerViewAdapter).values = data
			stopLoading(data.isNotEmpty())
		}
		viewModel.error.observe(viewLifecycleOwner) {
			Singleton.handleResponse(response = it, context as Activity, TAG)
			ProgressDialogUtil.dismissProgressDialog()
		}
		viewModel.isLoading.observe(viewLifecycleOwner) {
			if (it) {
				ProgressDialogUtil.showProgressDialog(context as DashboardActivity)
				swipeRefresh.isEnabled = false
			} else {
				ProgressDialogUtil.dismissProgressDialog()
				swipeRefresh.isEnabled = true
			}
		}

		viewModel.noInternet.observe(viewLifecycleOwner) {
			Toast.makeText(context, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
		}
	}

	override fun update() {
		super.update()

		load()
	}

	private fun startLoading() {
		shimmerLayout.show()
		shimmerLayout.startShimmer()

		recyclerView.hide()
		noDataView.hide()
	}

	private fun stopLoading(isDataAvailable: Boolean) {
		val shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
		val view = if (isDataAvailable) recyclerView else noDataView
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
			.setUpdateListener {
				shimmerLayout.hide()
			}

		swipeRefresh.isRefreshing = false
	}

	private fun load() {
		startLoading()
		viewModel.getTeams(page = 0)
	}

	private class TeamRecyclerViewAdapter : BaseRecyclerViewAdapter<Member>(R.layout.layout_teams_item) {
		override fun bindView(holder: ViewHolder, item: Member, position: Int) {
			super.bindView(holder, item, position)

			with(holder.itemView) {
				val avatarView = findViewById<ImageView>(R.id.avatarView)
				Glide.with(context).load(item.pictureUrl).into(avatarView)

				val nameView = findViewById<TextView>(R.id.nameView)
				nameView.text = item.name

				val lastBlessedView = findViewById<TextView>(R.id.lastBlessedView)
				lastBlessedView.text = item.lastBlessedAt

				val lastAppreciatedView = findViewById<TextView>(R.id.lastAppreciatedView)
				lastAppreciatedView.text = item.lastAppreciatedAt
			}
		}
	}
}