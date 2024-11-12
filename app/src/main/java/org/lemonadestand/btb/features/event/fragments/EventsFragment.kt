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
import org.lemonadestand.btb.constants.SelectedEvent
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
	private var selectButton = SelectedEvent.SCHEDULE
	private var eventClick = ClickType.COMMON


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

	private fun handleClickEvents() {
		mBinding.tabView.onSelect = {
			when (it) {
				0 -> {
					selectButton = SelectedEvent.SCHEDULE
					val fragment = ScheduleEventFragment()
					fragment.onSelect = {
						navController.navigate(EventsFragmentDirections.toDetail(it))
					}
					val bundle = Bundle().apply {
						putParcelable("user", Utils.getEventUser(context))
					}
					fragment.arguments = bundle
					setFragment(fragment)
				}
				1 -> {
					selectButton = SelectedEvent.PAST
					val fragment = PastEventFragment()
					fragment.onSelect = {
						navController.navigate(EventsFragmentDirections.toDetail(it))
					}
					val bundle = Bundle().apply {
						putParcelable("user", Utils.getEventUser(context))
					}
					fragment.arguments = bundle
					setFragment(fragment)
				}
				2 -> {
					selectButton = SelectedEvent.RECORDED
					val fragment = RecordedEventFragment()
					val bundle = Bundle().apply {
						putParcelable("user", Utils.getEventUser(context))
					}
					fragment.arguments = bundle
					setFragment(fragment)
				}
			}
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
		when (selectButton) {
			SelectedEvent.SCHEDULE -> {
				mBinding.tabView.selection = 0
			}

			SelectedEvent.PAST -> {
				mBinding.tabView.selection = 1
			}

			else -> {
				mBinding.tabView.selection = 2
			}
		}
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