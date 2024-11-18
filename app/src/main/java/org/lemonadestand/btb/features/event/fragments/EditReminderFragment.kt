package org.lemonadestand.btb.features.event.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.aisynchronized.helper.DateHelper
import com.google.gson.Gson
import org.lemonadestand.btb.App
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.core.models.EventFor
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import org.lemonadestand.btb.features.common.fragments.SelectContactListFragment
import org.lemonadestand.btb.features.common.fragments.SelectMultiUsersBottomSheetFragment
import org.lemonadestand.btb.features.common.fragments.UserListFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.features.event.models.EventModel
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.EventRepository
import org.lemonadestand.btb.mvvm.viewmodel.EventViewModel
import org.lemonadestand.btb.utils.Utils
import java.util.Calendar
import java.util.Date
import kotlin.math.max
import kotlin.math.min

class EditReminderFragment : BaseFragment(R.layout.fragment_edit_reminder) {
	private val args: EditReminderFragmentArgs by navArgs()

	private lateinit var titleView: TextView
	private lateinit var descriptionView: TextView
	private lateinit var dateView: TextView

	private lateinit var btnOnce: TextView
	private lateinit var btnYearly: TextView
	private lateinit var repeatingView: LinearLayout
	private lateinit var repeatSwitch: SwitchCompat

	private lateinit var reminderView: TextView
	private lateinit var btnReminderMinus: TextView
	private lateinit var btnReminderPlus: TextView

	private lateinit var notifyView: TextView

	private lateinit var btnTeam: TextView
	private lateinit var btnContact: TextView

	private lateinit var btnEventTeamMember: LinearLayout
	private lateinit var eventTeamMemberView: TextView

	private lateinit var btnEventContact: LinearLayout
	private lateinit var eventContactView: TextView

	private lateinit var btnEventCompany: LinearLayout
	private lateinit var eventCompanyView: TextView

	private lateinit var viewModel: EventViewModel
	private val calendar by lazy { Calendar.getInstance() }

	private var event: EventModel? = null
		set(value) {
			field = value
			handleEvent()
		}

	private var title: String = ""
		set(value) {
			field = value
			if (this::titleView.isInitialized) titleView.text = value
		}
	private var description: String = ""
		set(value) {
			field = value
			if (this::descriptionView.isInitialized) descriptionView.text = value
		}
	private var dateOfEvent: Date? = Date()
		set(value) {
			field = value ?: return
			if (this::dateView.isInitialized) dateView.text = DateHelper.format(value, "EE, MMM d, yyyy")
		}
	private var frequency: EventModel.Frequency = EventModel.Frequency.once
		set(value) {
			field = value
			if (!this::btnOnce.isInitialized || !this::btnYearly.isInitialized || !this::repeatingView.isInitialized) return
			btnOnce.setBackgroundResource(if (value == EventModel.Frequency.once) R.drawable.back_for_all else 0)
			btnYearly.setBackgroundResource(if (value == EventModel.Frequency.yearly) R.drawable.back_for_all else 0)
			repeatingView.visibility = if (value == EventModel.Frequency.yearly) View.VISIBLE else View.GONE
		}
	private var repeating: String = "0"
		set(value) {
			field = value
			if (!this::repeatSwitch.isInitialized) return
			repeatSwitch.isChecked = value == "1"
		}
	private var reminderDays: Long = 0
		set(value) {
			field = value
			if (!this::reminderView.isInitialized || !this::btnReminderPlus.isInitialized || !this::btnReminderMinus.isInitialized) return
			reminderView.text = if (value == 0L) "No Reminder" else "$value day(s) in advance"
			btnReminderPlus.isClickable = value < 100
			btnReminderPlus.alpha = if (value < 100) 1.0f else 0.5f
			btnReminderMinus.isClickable = value > 0
			btnReminderMinus.alpha = if (value > 0) 1.0f else 0.5f
		}
	private var notifyUsers = arrayListOf<String>()
		set(value) {
			field = value
			if (!this::notifyView.isInitialized) return
			notifyView.text = if (value.isEmpty()) "" else value.joinToString(",\n")
		}
	private var eventFor: EventFor = EventFor.team
		set(value) {
			field = value

			if (this::btnTeam.isInitialized) btnTeam.setBackgroundResource(if (value == EventFor.team) R.drawable.back_for_all else 0)
			if (this::btnContact.isInitialized) btnContact.setBackgroundResource(if (value == EventFor.contact) R.drawable.back_for_all else 0)

			if (this::btnEventTeamMember.isInitialized) btnEventTeamMember.visibility = if (value == EventFor.team) View.VISIBLE else View.GONE
			if (this::btnEventContact.isInitialized) btnEventContact.visibility = if (value == EventFor.contact) View.VISIBLE else View.GONE
			if (this::btnEventCompany.isInitialized) btnEventCompany.visibility = if (value == EventFor.company) View.VISIBLE else View.GONE
		}
	private var selectedTeamMember: UserListModel? = null
		set(value) {
			field = value
			if (this::eventTeamMemberView.isInitialized) eventTeamMemberView.text = value?.name ?: ""
		}
	private var selectedContact: UserListModel? = null
		set(value) {
			field = value
			if (this::eventContactView.isInitialized) eventContactView.text = value?.name ?: ""
		}

	override fun resolveArguments() {
		super.resolveArguments()
		event = args.event

		val navTitleView = rootView.findViewById<TextView>(R.id.navTitleView)
		navTitleView.text = if (event != null) "Edit Reminder" else "New Reminder"
	}

	override fun init() {
		super.init()

		val viewModelProviderFactory = CommonViewModelFactory(App.instance, EventRepository())
		viewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory)[EventViewModel::class.java]

		val btnBack = rootView.findViewById<ImageView>(R.id.btnBack)
		btnBack.setOnSingleClickListener { navController.navigateUp() }

		val btnSave = rootView.findViewById<TextView>(R.id.btnSave)
		btnSave.setOnSingleClickListener { handleSave() }

		val btnTitle = rootView.findViewById<LinearLayout>(R.id.btnTitle)
		btnTitle.setOnSingleClickListener {
			val arguments = Bundle().apply {
				putString("title", "Edit Title")
				putString("message", title)
			}
			WriteTextFragment().let {
				it.arguments = arguments
				it.onChange = { value -> title = value }
				it.show(childFragmentManager, it.tag)
			}
		}
		titleView = rootView.findViewById(R.id.titleView)

		val btnDescription = rootView.findViewById<LinearLayout>(R.id.btnDescription)
		btnDescription.setOnSingleClickListener {
			val arguments = Bundle().apply {
				putString("title", "Edit Description")
				putString("message", description)
			}
			WriteTextFragment().let {
				it.arguments = arguments
				it.onChange = { value -> description = value }
				it.show(childFragmentManager, it.tag)
			}
		}
		descriptionView = rootView.findViewById(R.id.descriptionView)

		val btnSelectDate = rootView.findViewById<LinearLayout>(R.id.btnSelectDate)
		btnSelectDate.setOnSingleClickListener {
			handleSelectDate()
		}
		dateView = rootView.findViewById(R.id.dateView)
		dateOfEvent = Date()

		btnOnce = rootView.findViewById(R.id.btnOnce)
		btnOnce.setOnSingleClickListener { frequency = EventModel.Frequency.once }
		btnYearly = rootView.findViewById(R.id.btnYearly)
		btnYearly.setOnSingleClickListener { frequency = EventModel.Frequency.yearly }

		repeatingView = rootView.findViewById(R.id.repeatingView)
		repeatSwitch = rootView.findViewById(R.id.repeatSwitch)
		repeatSwitch.setOnCheckedChangeListener { _, isChecked -> repeating = if (isChecked) "1" else "0" }
		frequency = EventModel.Frequency.once

		reminderView = rootView.findViewById(R.id.reminderView)
		btnReminderMinus = rootView.findViewById(R.id.btnReminderMinus)
		btnReminderMinus.setOnSingleClickListener { reminderDays = min(max(0, reminderDays - 1), 100) }
		btnReminderPlus = rootView.findViewById(R.id.btnReminderPlus)
		btnReminderPlus.setOnSingleClickListener { reminderDays = min(max(0, reminderDays + 1), 100) }

		val btnNotify = rootView.findViewById<LinearLayout>(R.id.btnNotify)
		btnNotify.setOnSingleClickListener {
			val fragment = SelectMultiUsersBottomSheetFragment()
			fragment.arguments = Bundle().apply {
				putString("title", "Select Team Members")
				putString("list", Gson().toJson(notifyUsers.map { UserListModel(username = it) }))
			}
			fragment.setCallback(object : OnItemClickListener {
				override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
					val list = `object` as ArrayList<UserListModel>?
					if (list.isNullOrEmpty()) {
						notifyUsers = arrayListOf()
					} else {
						val users = arrayListOf<String>()
						for (i in 0 until list.size) {
							users.add(list[i].username)
						}
						notifyUsers = users
					}
				}
			})
			fragment.show(childFragmentManager, fragment.tag)
		}
		notifyView = rootView.findViewById(R.id.notifyView)

		btnTeam = rootView.findViewById(R.id.btnTeam)
		btnTeam.setOnSingleClickListener { eventFor = EventFor.team }

		btnContact = rootView.findViewById(R.id.btnContact)
		btnContact.setOnSingleClickListener { eventFor = EventFor.contact }

		btnEventTeamMember = rootView.findViewById(R.id.btnEventTeamMember)
		btnEventTeamMember.setOnSingleClickListener {
			val fragment = UserListFragment()
			fragment.setCallback(object : OnItemClickListener {
				override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
					selectedTeamMember = (`object` as UserListModel)
					fragment.dismiss()
				}
			})
			fragment.show(childFragmentManager, fragment.tag)
		}
		eventTeamMemberView = rootView.findViewById(R.id.eventTeamMemberView)

		btnEventContact = rootView.findViewById(R.id.btnEventContact)
		btnEventContact.setOnSingleClickListener {
			val fragment = SelectContactListFragment()
			fragment.setCallback(object : OnItemClickListener {
				override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
					selectedContact = (`object` as UserListModel)
					fragment.dismiss()
				}
			})
			fragment.show(childFragmentManager, fragment.tag)
		}
		eventContactView = rootView.findViewById(R.id.eventContactView)

		btnEventCompany = rootView.findViewById(R.id.btnEventCompany)
		eventCompanyView = rootView.findViewById(R.id.eventCompanyView)

		eventFor = EventFor.team
	}

	private fun handleEvent() {
		event ?: return

		title = event!!.title
		description = event!!.description
		dateOfEvent = event!!.start
		frequency = EventModel.Frequency.valueOf(event!!.frequency)
		repeating = event!!.repeating

		val startTime = event?.start?.time
		val reminderTime = event?.reminder?.time
		if (startTime != null && reminderTime != null) {
			val diff = startTime - reminderTime
			reminderDays = diff / (24 * 60 * 60 * 1000)
		}

		notifyUsers = event!!.notify

		eventFor = if (event!!.resource.id.contains(EventFor.team.value)) EventFor.team
		else if (event!!.resource.id.contains(EventFor.contact.value)) EventFor.contact
		else if (event!!.resource.id.contains(EventFor.company.value)) EventFor.company
		else EventFor.team
	}

	private fun handleSelectDate() {
		val year = calendar!![Calendar.YEAR]
		val month = calendar!![Calendar.MONTH]
		val day = calendar!![Calendar.DAY_OF_MONTH]
		DatePickerDialog(
			requireActivity(),
			{ _: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int ->
				calendar!![Calendar.YEAR] = year1
				calendar!![Calendar.MONTH] = month1
				calendar!![Calendar.DAY_OF_MONTH] = dayOfMonth
				dateOfEvent = calendar.time
			}, year, month, day
		).apply {
			datePicker.minDate = Date().time
			show()
		}
	}

	private fun handleSave() {
		if (title.isEmpty()) {
			Toast.makeText(requireActivity(), "Please add an event title.", Toast.LENGTH_SHORT).show()
			return
		}

		if (event == null) {
			handleCreate()
			return
		}

		val currentUser = Utils.getUser(requireActivity())

		var resource = ""
		if (eventFor == EventFor.team) {
			if (selectedTeamMember == null) {
				if (event!!.parent == null) {
					resource = event!!.resource.id
					resource = resource.replace("user/", "")
				} else {
					Toast.makeText(requireActivity(), "Please select user", Toast.LENGTH_SHORT).show()
				}
			} else {
				resource = selectedTeamMember!!.uniqueId
				resource = resource.replace("user/", "")
			}
		} else {
			if (selectedContact == null) {
				if (event!!.parent != null) {
					resource = event!!.resource.id
					resource = resource.replace("contact/", "")
				} else {
					Toast.makeText(requireActivity(), "Please select contact.", Toast.LENGTH_SHORT).show()
				}
			} else {
				resource = selectedContact!!.uniqueId
			}
		}

		val calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, -reminderDays.toInt())
		val reminderDate = calendar.time

		val requestModel = ReminderRequestBody(
			title = title,
			description = description,
			start = dateOfEvent!!,
			end = dateOfEvent!!,
			reminder = reminderDate,
			frequency = frequency.value,
			repeating = repeating,
			notify = notifyUsers,
			resource = if (eventFor == EventFor.team) "user/${resource}" else "contact/${resource}",
		)
		viewModel.editReminder(requestModel, event!!.uniqueId)

		navController.navigateUp()
	}

	private fun handleCreate() {
		if (title.isEmpty()) {
			Toast.makeText(requireActivity(), "Please add an event title.", Toast.LENGTH_SHORT).show()
			return
		}

		val currentUser = Utils.getUser(requireActivity())

		var resource = ""
		if (eventFor == EventFor.team) {
			resource = if (selectedTeamMember == null) currentUser.uniqueId else selectedTeamMember!!.uniqueId

		} else {
			if (selectedContact == null) {
				Toast.makeText(requireActivity(), "Please select contact.", Toast.LENGTH_SHORT).show()
				return
			} else {
				resource = selectedContact!!.uniqueId
			}
		}

		val calendar = Calendar.getInstance()
		calendar.add(Calendar.DATE, -reminderDays.toInt())
		val reminderDate = calendar.time

		val request = ReminderRequestBody(
			title = title,
			description = description,
			start = dateOfEvent!!,
			end = dateOfEvent!!,
			reminder = reminderDate,
			frequency = frequency.value,
			repeating = repeating,
			notify = notifyUsers,
			resource = if (eventFor == EventFor.team) "user/$resource" else "contact/$resource"
		)
		viewModel.addReminder(request)

		navController.navigateUp()
	}
}