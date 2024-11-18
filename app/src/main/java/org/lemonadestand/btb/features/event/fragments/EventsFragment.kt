package org.lemonadestand.btb.features.event.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.databinding.FragmentEventsBinding
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.fragments.TeamAndContactsFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.utils.Storage
import org.lemonadestand.btb.utils.Utils
import java.util.Locale

class EventsFragment : BaseFragment(R.layout.fragment_events) {

	private lateinit var mBinding: FragmentEventsBinding

	private var currentFragment: Fragment? = null

	private var tabIndex: Int = Storage.eventsTabIndex
		set(value) {
			field = value
			Storage.eventsTabIndex = value
			handleTabIndex()
		}
	private var selectedUser: UserListModel? = Utils.getResource(context)
		set(value) {
			field = value
			Utils.setResource(context, value)
			handleSelectedUser()
		}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentEventsBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)
		handleSelectedUser()

		mBinding.tabView.selection = tabIndex
		mBinding.tabView.onSelect = {
			tabIndex = it
		}
		handleTabIndex()

		mBinding.userCard.setOnClickListener {
			val teamAndContactsFragment = TeamAndContactsFragment()
			teamAndContactsFragment.onSelect = {
				selectedUser = it
				teamAndContactsFragment.dismiss()
			}
			teamAndContactsFragment.arguments = Bundle().apply {
				putString("title", "Show Event For :")
				putBoolean("is_event", true)
			}
			teamAndContactsFragment.show(requireActivity().supportFragmentManager, teamAndContactsFragment.tag)
		}

		mBinding.btnFloatingEvent.setOnSingleClickListener { navController.navigate(EventsFragmentDirections.toDetail()) }

		return mBinding.root
	}

	private fun handleTabIndex() {
		when (tabIndex) {
			0 -> {
				if (currentFragment is ScheduledEventsFragment) return
				currentFragment = ScheduledEventsFragment().apply {
					onSelect = {
						navController.navigate(EventsFragmentDirections.toDetail(it))
					}
					arguments = Bundle().apply {
						putParcelable("user", Utils.getResource(context))
					}
					setFragment(this)
				}
				mBinding.btnFloatingEvent.visibility = View.VISIBLE
			}

			1 -> {
				if (currentFragment is PastEventFragment) return
				currentFragment = PastEventFragment().apply {
					onSelect = {
						navController.navigate(EventsFragmentDirections.toDetail(it))
					}
					arguments = Bundle().apply {
						putParcelable("user", Utils.getResource(context))
					}
					setFragment(this)
				}
				mBinding.btnFloatingEvent.visibility = View.GONE
			}

			2 -> {
				if (currentFragment is CompletedEventsFragment) return
				currentFragment = CompletedEventsFragment().apply {
					arguments = Bundle().apply {
						putParcelable("user", Utils.getResource(context))
					}
					setFragment(this)
				}
				mBinding.btnFloatingEvent.visibility = View.VISIBLE
			}
		}
	}

	private fun handleSelectedUser() {
		if (selectedUser?.picture != null) {
			Glide.with(requireContext()).load(selectedUser?.picture).into(mBinding.ivImage)
		} else {
			val imageI = selectedUser?.name?.lowercase(Locale.ROOT)?.getImageUrlFromName()
			Glide.with(requireContext()).load(imageI).into(mBinding.ivImage)
		}

		if (currentFragment is ScheduledEventsFragment) (currentFragment as ScheduledEventsFragment).refreshData()
		if (currentFragment is PastEventFragment) (currentFragment as PastEventFragment).refreshData()
		if (currentFragment is CompletedEventsFragment) (currentFragment as CompletedEventsFragment).refreshData()
	}

	private fun setFragment(fragment: Fragment?) {
		childFragmentManager.beginTransaction().replace(R.id.event_fragment, fragment!!).commit()
	}
}