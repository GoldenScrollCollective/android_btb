package org.lemonadestand.btb.features.event.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
		mBinding.tvScheduled.setOnClickListener { view ->
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
			setDefaultView()
			view.alpha = 1f


			view.setBackgroundResource(R.drawable.back_for_all)
			view.elevation = 5f


			mBinding.tvScheduled.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
		}
		mBinding.tvScheduled.performClick()

		mBinding.tvPast.setOnClickListener { view ->
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
			setDefaultView()
			view.alpha = 1f
			view.setBackgroundResource(R.drawable.back_for_all)
			view.elevation = 5f
			mBinding.tvPast.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
		}

		mBinding.tvRecorded.setOnClickListener { view ->
			selectButton = SelectedEvent.RECORDED
			val fragment = RecordedEventFragment()
			val bundle = Bundle().apply {
				putParcelable("user", Utils.getEventUser(context))
			}
			fragment.arguments = bundle
			setFragment(fragment)
			setDefaultView()
			view.alpha = 1f
			view.setBackgroundResource(R.drawable.back_for_all)
			view.elevation = 5f
			mBinding.tvRecorded.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
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

	private fun setDefaultView() {
		mBinding.tvScheduled.alpha = 0.7f
		mBinding.tvPast.alpha = 0.7f
		mBinding.tvRecorded.alpha = 0.7f
		mBinding.tvScheduled.setBackgroundResource(0)
		mBinding.tvPast.setBackgroundResource(0)
		mBinding.tvRecorded.setBackgroundResource(0)

		mBinding.tvScheduled.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
		mBinding.tvRecorded.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
		mBinding.tvPast.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_grey))
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
				mBinding.tvScheduled.performClick()
			}

			SelectedEvent.PAST -> {
				mBinding.tvPast.performClick()
			}

			else -> {
				mBinding.tvRecorded.performClick()
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