package org.lemonadestand.btb.features.event.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.SelectedRecord
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.core.repositories.EventRepository
import org.lemonadestand.btb.core.viewModels.EventViewModel
import org.lemonadestand.btb.databinding.ActivityEditRecordBinding
import org.lemonadestand.btb.features.common.fragments.UserListFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.RecordRequestBody
import org.lemonadestand.btb.features.event.fragments.WriteTextFragment
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.singleton.Singleton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditRecordActivity : AppCompatActivity(), OnItemClickListener {

	lateinit var mBinding: ActivityEditRecordBinding
	private var bottomSheetFragmentUser: UserListFragment? = null
	private var calendar: Calendar? = null
	private var bottomSheetFragmentText: WriteTextFragment? = null
	private var selectedType = SelectedRecord.COMMON
	var title = ""
	var description = ""
	private var selectedUserListModel: UserListModel? = null
	private lateinit var viewModel: EventViewModel
	var tag = "AddRecordActivity"
	private var dateOfEvent = ""
	private var eventModel: Event? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mBinding = ActivityEditRecordBinding.inflate(layoutInflater)
		setContentView(mBinding.root)
		getData()
		setClicks()
		appendCommonData()
		setUpViewModel()
	}

	private fun getData() {
		val bundle = intent.extras
		if (bundle != null) {
			eventModel = bundle.getParcelable("event_data")

			Log.e("eventModel=>", eventModel.toString())
		}
	}

	private fun appendCommonData() {
		calendar = Calendar.getInstance()
		mBinding.txtUser.text = eventModel!!.blessingByUser!!.name


		dateOfEvent = eventModel?.blessingComplete.toString()
		mBinding.tvDateSelect.text = getDate(eventModel!!.blessingComplete!!)
		mBinding.txtTitle.text = eventModel!!.blessingSent
		mBinding.txtNotes.text = eventModel!!.blessingNotes
		mBinding.etPrice.setText(eventModel!!.blessingValue)

	}


	private fun setClicks() {
		mBinding.btnBack.setOnClickListener { finish() }
		mBinding.selectUser.setOnClickListener {
			showBottomSheetUser()
		}
		mBinding.titleSet.setOnClickListener {
			selectedType = SelectedRecord.TITLE
			val content = mBinding.txtTitle.text.toString()
			if (content != getString(R.string.set_value)) {
				title = content
			}
			showBottomSheetMessage(title)
		}

		mBinding.setNotes.setOnClickListener {
			selectedType = SelectedRecord.DESCRIPTION
			val content = mBinding.txtNotes.text.toString()
			if (content != getString(R.string.set_description)) {
				description = content
			}
			showBottomSheetMessage(description)
		}
		mBinding.selectDate.setOnClickListener { showDatePickerDialog() }

		mBinding.btnSave.setOnClickListener {
			checkValidation()
		}

	}

	private fun checkValidation() {
		if (mBinding.txtTitle.text.isEmpty()) {
			Toast.makeText(this, "Please add what was sent.", Toast.LENGTH_SHORT).show()
			return
		}

		var resource = ""
		//dateOfEvent = mBinding.tvDateSelect.text


		if (selectedUserListModel == null) {
			resource = eventModel!!.resource!!.id

			resource = resource.replace("user/", "")

		} else {
			resource = selectedUserListModel!!.uniqueId
		}


		val blessValue = mBinding.etPrice.text.toString()
		if (blessValue.contains("$")) {
			blessValue.replace("$", "")
		}

		val requestModel = RecordRequestBody(
			start = dateOfEvent,
			end = dateOfEvent,
			blessing_notes = mBinding.txtNotes.text.toString(),
			frequency = "once",
			resource = "user/${resource}",
			blessing_sent = mBinding.txtTitle.text.toString(),
			blessing_by_user_id = resource,
			blessing_complete = dateOfEvent,
			blessing_value = blessValue
		)
		viewModel.editRecord(requestModel, eventModel!!.uniqueId)
	}

	private fun showBottomSheetUser() {
		selectedType = SelectedRecord.SELECT_USER
		if (bottomSheetFragmentUser == null) {
			val fragmentManager: FragmentManager = supportFragmentManager
			bottomSheetFragmentUser = UserListFragment()
			bottomSheetFragmentUser?.setCallback(this)
			bottomSheetFragmentUser?.show(fragmentManager, bottomSheetFragmentUser!!.tag)

		} else {
			bottomSheetFragmentUser!!.show(supportFragmentManager, bottomSheetFragmentUser!!.tag)
		}
	}

	private fun showBottomSheetMessage(text: String) {

		val fragmentManager: FragmentManager = supportFragmentManager
		bottomSheetFragmentText = WriteTextFragment()
		bottomSheetFragmentText?.setCallback(this)
		val args = Bundle()
		args.putString("message", text)
		bottomSheetFragmentText?.arguments = args
		bottomSheetFragmentText?.show(fragmentManager, bottomSheetFragmentText!!.tag)
	}

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {

		setDataToUi(`object`!!)
	}

	private fun setDataToUi(text: Any) {
		when (selectedType) {
			SelectedRecord.TITLE -> {
				if (text.toString().isNotEmpty()) {
					mBinding.txtTitle.text = text.toString()
				}

			}

			SelectedRecord.DESCRIPTION -> {
				if (text.toString().isNotEmpty()) {
					mBinding.txtNotes.text = text.toString()
				}

			}

			SelectedRecord.SELECT_USER -> {
				bottomSheetFragmentUser?.dismiss()
				selectedUserListModel = (text as UserListModel)
				mBinding.txtUser.text = text.name
			}

			else -> {
				//
			}
		}

	}

	private fun showDatePickerDialog() {
		val year = calendar!![Calendar.YEAR]
		val month = calendar!![Calendar.MONTH]
		val day = calendar!![Calendar.DAY_OF_MONTH]
		val datePickerDialog = DatePickerDialog(
			this,
			{ _: DatePicker?, year1: Int, month1: Int, dayOfMonth: Int ->
				calendar!![Calendar.YEAR] = year1
				calendar!![Calendar.MONTH] = month1
				calendar!![Calendar.DAY_OF_MONTH] = dayOfMonth
				val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
				val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

				// Format the current date and time to the desired format
				val formattedDate = dateFormat.format(calendar!!.time)
				val formattedDate1 = dateFormat1.format(calendar!!.time)

				dateOfEvent = formattedDate1
				mBinding.tvDateSelect.text = formattedDate
			}, year, month, day
		)
		datePickerDialog.show()
	}

	private fun setUpViewModel() {

		val repository = EventRepository()
		val viewModelProviderFactory =
			CommonViewModelFactory((this).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]


		viewModel.error.observe(this) {
			Singleton.handleResponse(response = it, this, tag)
			ProgressDialogUtil.dismissProgressDialog()
		}

		viewModel.commonResponse.observe(this) {
			handleCommonResponse(this, it)
			ProgressDialogUtil.dismissProgressDialog()
		}


		viewModel.isLoading.observe(this) {
			Log.e("value==>", it.toString())
			if (it) {
				ProgressDialogUtil.showProgressDialog(this)
			} else {
				ProgressDialogUtil.dismissProgressDialog()
			}
		}

		viewModel.noInternet.observe(this) {
			Toast.makeText(this, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
		}
	}
}