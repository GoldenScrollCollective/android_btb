package org.lemonadestand.btb.features.event.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.getImageUrlFromName
import org.lemonadestand.btb.databinding.FragmentEventsBinding
import org.lemonadestand.btb.features.common.fragments.TeamAndContactsFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.utils.Utils
import java.util.Locale

class EventsFragment : BaseFragment(R.layout.fragment_events), OnItemClickListener {

	private val bottomSheetFragmentMessage = TeamAndContactsFragment()
	private lateinit var mBinding: FragmentEventsBinding
	private var eventClick = ClickType.COMMON

	private var currentFragment: Fragment? = null

	private var tabIndex = 0
		set(value) {
			field = value
			handleTabIndex()
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
		handleClickEvents()
		handleSelectedData()
		return mBinding.root

	}

	override fun update() {
		super.update()

		handleTabIndex()
	}

	private fun handleTabIndex() {
		when (tabIndex) {
			0 -> {
				if (currentFragment is ScheduleEventFragment) return
				currentFragment = ScheduleEventFragment().apply {
					onSelect = {
						navController.navigate(EventsFragmentDirections.toDetail(it))
					}
					arguments = Bundle().apply {
						putParcelable("user", Utils.getEventUser(context))
					}
					setFragment(this)
				}
			}
			1 -> {
				if (currentFragment is PastEventFragment) return
				currentFragment = PastEventFragment().apply {
					onSelect = {
						navController.navigate(EventsFragmentDirections.toDetail(it))
					}
					arguments = Bundle().apply {
						putParcelable("user", Utils.getEventUser(context))
					}
					setFragment(this)
				}
			}
			2 -> {
				if (currentFragment is RecordedEventFragment) return
				currentFragment = RecordedEventFragment().apply {
					arguments = Bundle().apply {
						putParcelable("user", Utils.getEventUser(context))
					}
					setFragment(this)
				}
			}
		}
	}

	private fun handleClickEvents() {
		mBinding.tabView.onSelect = {
			tabIndex = it
		}

		mBinding.userCard.setOnClickListener {
			showBottomSheetMessage()
		}
	}

	private fun handleSelectedData() {
		val user = Utils.getEventUser(context)
		if (user != null) {
			setSelectUserData(
				user.picture,
				user.name
			)
		} else {
			setSelectUserData(
				Utils.getUser(context).organization.picture,
				"All Users"
			)
		}
	}


	private fun setFragment(fragment: Fragment?) {
		childFragmentManager.beginTransaction().replace(R.id.event_fragment, fragment!!).commit()
	}

	private fun showBottomSheetMessage() {
		eventClick =  ClickType.SELECT_USER
		val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
		bottomSheetFragmentMessage.setCallback(this)
		val args = Bundle()
		args.putString("title", "Show Event For :")
		args.putBoolean("is_event", true)
		bottomSheetFragmentMessage.arguments = args
		bottomSheetFragmentMessage.show(fragmentManager, bottomSheetFragmentMessage.tag)
	}

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
		val data = `object` as UserListModel
		Log.e("data=>", data.name)
		when (eventClick) {
			ClickType.SELECT_USER -> {
				bottomSheetFragmentMessage.dismiss()
				setSelectUserData(data.picture, userName = data.name)
				callApi()
			}

			ClickType.EDIT_EVENT -> {
				Log.e("eventFragment=>","editEvent")
			}

			ClickType.DELETE_EVENT -> {

				Log.e("eventFragment=>","deleteEvent")
			}

			else -> {

			}
		}


	}

	private fun callApi() {
		handleTabIndex()
	}

	private fun setSelectUserData(image: String?, userName: String) {
		//https://ui-avatars.com/api/?name=lee guang
		if (image != null) {
			Glide.with(requireContext()).load(image).into(mBinding.ivImage)
		} else {
			var userName1 = ""
			val imageI = userName.lowercase(Locale.ROOT).getImageUrlFromName()
			Glide.with(requireContext()).load(imageI).into(mBinding.ivImage)
		}
	}
}