package org.lemonadestand.btb.features.event.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.SelectedReminder
import org.lemonadestand.btb.databinding.ActivityEditReminderBinding
import org.lemonadestand.btb.constants.getDate
import org.lemonadestand.btb.constants.getDateDifference
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.extensions.hide
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.features.event.models.EventModel
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.EventRepository
import org.lemonadestand.btb.mvvm.viewmodel.EventViewModel
import org.lemonadestand.btb.features.common.fragments.SelectContactListFragment
import org.lemonadestand.btb.features.common.fragments.UserListFragment
import org.lemonadestand.btb.features.common.fragments.UserListFragmentMulti
import org.lemonadestand.btb.features.event.fragments.WriteTextFragment
import org.lemonadestand.btb.extensions.show
import org.lemonadestand.btb.singleton.Singleton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditReminderActivity : AppCompatActivity(), OnItemClickListener {
    lateinit var mBinding: ActivityEditReminderBinding

    var tag = "AddReminderActivity"
    private var bottomSheetFragmentMultiUser: UserListFragmentMulti? = null
    private var bottomSheetFragmentUser: UserListFragment? = null
    private var calendar: Calendar? = null

    private var selectedType = SelectedReminder.COMMON
    private var daysReminder = 0
    private var localTitle: String? = null
    private var bottomSheetFragmentText: WriteTextFragment? = null
    private var selectedContactListModel: UserListModel? = null
    private var bottomSheetFragmentContact: SelectContactListFragment? = null
    private var reminderUerList: ArrayList<UserListModel> = ArrayList()

    private lateinit var viewModel: EventViewModel


    var title = ""
    var description = ""
    var dateOfEvent = ""
    var reminderDate = ""
    private var frequency = "once"

    //var reminderDayCount = "0"
    private var repeatEvent = ""
    private var eventFor: String = "Team"
    private var reminderUerListJson: ArrayList<String> = ArrayList()
    private var selectedUserListModel: UserListModel? = null


     private var eventModel : EventModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)
        mBinding = ActivityEditReminderBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        getData()
        setClicks()
        setUpViewModel()
        handleUIChanges()
    }

    private fun getData() {
        val bundle = intent.extras
        if(bundle!=null) {
            eventModel = bundle.getParcelable("event_data")

            eventFor = if (eventModel!!.parent == null ) {
                "Team"
            } else {
                "Contact"
            }

            Log.e("eventModel=>",eventModel.toString())
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun handleUIChanges() {
        calendar = Calendar.getInstance()






        Log.e("dateOfEvent=>", dateOfEvent)
        mBinding.lnRepeating.hide()
        mBinding.dividerRepeat.hide()
        mBinding.btnOnce.setBackgroundResource(R.drawable.back_for_all)
        mBinding.btnOnce.elevation = 5f
        mBinding.btnYearly.setBackgroundResource(0)



        mBinding.tvTeamMember.text = eventModel!!.resource.name
        dateOfEvent = eventModel?.start.toString()
        mBinding.tvDateSelect.text = getDate(eventModel!!.start)
        mBinding.tvTitleReminder.text = eventModel!!.title
        mBinding.tvDescriptionReminder.text = eventModel!!.description
        dateOfEvent = eventModel!!.start
        if(eventModel!!.frequency == "once")
        {
            mBinding.btnOnce.performClick()
        }
        else
        {
            mBinding.btnYearly.performClick()
        }

        if (eventModel!!.repeating == "1")
        {
            mBinding.repeatEventSwitch.isChecked = true
        }

        daysReminder =   getDateDifference(eventModel!!.created,eventModel!!.reminder!!)

        if (daysReminder != 0)
        {
            mBinding.txtReminder.text = buildString {
                append(daysReminder)
                append(" Days in Advance")
            }

        }
        reminderUerListJson = eventModel!!.notify
        mBinding.tvNotification.text = eventModel!!.notify.joinToString { "," }

        if(eventModel!!.parent != null)
        {
            mBinding.btnContact.performClick()
        }
        else
        {
            mBinding.btnTeam.performClick()
        }


       // eventModel.

    }

    private fun setClicks() {
        mBinding.btnBack.setOnClickListener { finish() }

        mBinding.btnTeam.setOnClickListener {
            handleUSer(true)
            eventFor = "Team"
            localTitle = "Select a User"
            mBinding.btnTeam.setBackgroundResource(R.drawable.back_for_all)
            mBinding.btnTeam.elevation = 5f
            mBinding.btnContact.setBackgroundResource(0)
        }

        mBinding.btnTeam.performClick()

        mBinding.btnContact.setOnClickListener { _ ->
            handleUSer(false)
            eventFor = "Contact"
            localTitle = "Select a Contact"
            mBinding.btnContact.setBackgroundResource(R.drawable.back_for_all)
            mBinding.btnContact.elevation = 5f
            mBinding.btnTeam.setBackgroundResource(0)
        }

        mBinding.selectUser.setOnClickListener {
            selectedType = SelectedReminder.TEAM_MEMBER
            if(eventFor == "Team")
            {
                showBottomSheetUser()
            }
            else{
                showBottomSheetContact()
            }

        }

        mBinding.setReminder.setOnClickListener {
            selectedType = SelectedReminder.NOTIFICATION
            showBottomSheetUserMulti()
        }

        mBinding.btnDecrease.setOnClickListener {
            if (daysReminder != 1) {
                daysReminder--
                mBinding.txtReminder.text = buildString {
                    append(daysReminder)
                    append(" Days in Advance")
                }
            }
        }

        mBinding.btnIncrease.setOnClickListener {
            daysReminder++
            mBinding.txtReminder.text = buildString {
                append(daysReminder)
                append(" Days in Advance")
            }
        }

        mBinding.titleSet.setOnClickListener {
            selectedType = SelectedReminder.TITLE
            val content = mBinding.tvTitleReminder.text.toString()
            if (content != getString(R.string.set_title_new)) {
                title = content
            }
            showBottomSheetMessage(title)
        }
        mBinding.descSet.setOnClickListener {
            selectedType = SelectedReminder.DESCRIPTION
            val content = mBinding.tvDescriptionReminder.text.toString()
            if (content != getString(R.string.set_description)) {
                description = content
            }

            showBottomSheetMessage(description)
        }

        mBinding.selectDate.setOnClickListener { showDatePickerDialog() }

        mBinding.btnYearly.setOnClickListener {
            frequency = "yearly"

            mBinding.lnRepeating.show()
            mBinding.dividerRepeat.show()
            mBinding.btnYearly.setBackgroundResource(R.drawable.back_for_all)
            mBinding.btnYearly.elevation = 5f
            mBinding.btnOnce.setBackgroundResource(0)
        }

        mBinding.btnOnce.setOnClickListener {
            frequency = "once"
            mBinding.lnRepeating.hide()
            mBinding.dividerRepeat.hide()
            mBinding.btnOnce.setBackgroundResource(R.drawable.back_for_all)
            mBinding.btnOnce.elevation = 5f
            mBinding.btnYearly.setBackgroundResource(0)
        }

        mBinding.repeatEventSwitch.setOnClickListener {
            repeatEvent = if (frequency == "yearly") {
                if (mBinding.repeatEventSwitch.isChecked) {
                    "1"
                } else {
                    "0"
                }
            } else {
                ""
            }

        }

        mBinding.btnSave.setOnClickListener { checkValidation() }
    }

    private fun checkValidation() {

        if (mBinding.tvTitleReminder.text.isEmpty()) {
            Toast.makeText(this, "Please add an event title.", Toast.LENGTH_SHORT).show()
            return
        }
        var resource = ""
        //dateOfEvent = mBinding.tvDateSelect.text

        if (eventFor == "Team")
        {
            if (selectedUserListModel == null) {
                if (eventModel!!.parent == null)
                {
                    resource = eventModel!!.resource.id
                    resource = resource.replace("user/","")
                }
                else{
                    Toast.makeText(this, "Please select user", Toast.LENGTH_SHORT).show()
                }

            } else {
                resource = selectedUserListModel!!.uniq_id
                resource = resource.replace("user/","")
            }
        }
        else
        {
            if (selectedContactListModel == null) {

                if(eventModel!!.parent != null )
                {
                    resource = eventModel!!.resource.id
                    resource = resource.replace("contact/","")
                }
                else
                {
                    Toast.makeText(this, "Please select contact.", Toast.LENGTH_SHORT).show()
                }


            } else {
                resource = selectedContactListModel!!.uniq_id
            }
        }


        val c = calendar
        c!!.add(Calendar.DATE, -daysReminder)
        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedDate1 = dateFormat1.format(c.time)
        reminderDate = formattedDate1


        val requestModel = ReminderRequestBody(
            start = dateOfEvent,
            end = dateOfEvent,
            title = mBinding.tvTitleReminder.text.toString(),
            reminder = reminderDate,
            frequency = frequency,
            repeating = repeatEvent,
            notify = reminderUerListJson,
            resource = if(eventFor=="Team") "user/${resource}" else "contact/${resource}",
            description = mBinding.tvDescriptionReminder.text.toString()
        )

        Log.e("resource=>",requestModel.resource)
        viewModel.editReminder(requestModel, eventModel!!.uniq_id)


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
                val dateFormat =
                    SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
                val formattedDate = dateFormat.format(calendar!!.time)


                val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val formattedDate1 = dateFormat1.format(calendar!!.time)
                dateOfEvent = formattedDate1

                dateOfEvent = formattedDate
                mBinding.tvDateSelect.text = formattedDate


            }, year, month, day
        )
        datePickerDialog.show()
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
            SelectedReminder.TITLE -> {
                if (text.toString().isNotEmpty()) {
                    mBinding.tvTitleReminder.text = text.toString()
                }
            }

            SelectedReminder.DESCRIPTION -> {
                if (text.toString().isNotEmpty()) {
                    mBinding.tvDescriptionReminder.text = text.toString()
                }
            }

            SelectedReminder.NOTIFICATION -> {
                bottomSheetFragmentMultiUser?.dismiss()

                val list = text as ArrayList<UserListModel>
                val textString: String
                if (list.isNullOrEmpty()) {
                    reminderUerList = ArrayList()
                    reminderUerListJson = ArrayList()
                    textString = getString(R.string.set_notification)

                } else {
                    reminderUerList = list
                    textString = reminderUerList.joinToString(separator = ", ") { it.username }
                    reminderUerListJson.clear()

                    for (i in 0 until reminderUerList.size) {
                        reminderUerListJson.add(reminderUerList[i].username)
                    }

                }

                mBinding.tvNotification.text = (textString)
            }

            SelectedReminder.TEAM_MEMBER -> {
                if(eventFor == "Team")
                {
                    bottomSheetFragmentUser?.dismiss()
                    selectedUserListModel = (text as UserListModel)
                    mBinding.tvTeamMember.text = text.name
                }
                else{
                    bottomSheetFragmentContact?.dismiss()
                    selectedContactListModel = (text as UserListModel)
                    mBinding.tvTeamMember.text = text.name
                }
            }

            else -> {
                //
            }
        }

    }
    private fun showBottomSheetContact() {
        if (bottomSheetFragmentContact == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            bottomSheetFragmentContact = SelectContactListFragment()
            bottomSheetFragmentContact?.setCallback(this)
            bottomSheetFragmentContact?.show(fragmentManager, bottomSheetFragmentContact!!.tag)

        } else {
            bottomSheetFragmentContact!!.show(supportFragmentManager, bottomSheetFragmentContact!!.tag)
        }

    }

    private fun showBottomSheetUser() {
        if (bottomSheetFragmentUser == null) {
            val fragmentManager: FragmentManager = supportFragmentManager
            bottomSheetFragmentUser = UserListFragment()
            bottomSheetFragmentUser?.setCallback(this)
            bottomSheetFragmentUser?.show(fragmentManager, bottomSheetFragmentUser!!.tag)

        } else {
            bottomSheetFragmentUser!!.show(supportFragmentManager, bottomSheetFragmentUser!!.tag)
        }

    }

    private fun showBottomSheetUserMulti() {

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragment = UserListFragmentMulti()
        val bundle = Bundle()
        val json = Gson().toJson(reminderUerList)
        bundle.putString("list", json)
        fragment.arguments = bundle

        bottomSheetFragmentMultiUser = fragment
        bottomSheetFragmentMultiUser?.setCallback(this)
        bottomSheetFragmentMultiUser?.show(fragmentManager, bottomSheetFragmentMultiUser!!.tag)


    }

    private fun setUpViewModel() {

        val repository = EventRepository()
        val viewModelProviderFactory =
            CommonViewModelFactory((this).application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]


        viewModel.liveError.observe(this) {
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
    private fun handleUSer(isTeam: Boolean) {

        if (isTeam) {
            mBinding.txtHeadingUser.text = buildString {
                append("LINK TO A TEAM MEMBER")
            }
            if (selectedUserListModel != null) {
                mBinding.tvTeamMember.text = selectedUserListModel!!.name
            } else
            {
                if (eventModel!!.parent == null)
                {
                    mBinding.tvTeamMember.text = eventModel!!.resource.name
                }
                else{
                    mBinding.tvTeamMember.text = "Select User"
                }


            }

        } else {
            mBinding.txtHeadingUser.text = buildString {
                append("LINK TO A CONTACT")
            }
            if (selectedContactListModel != null) {
                mBinding.tvTeamMember.text = selectedContactListModel!!.name
            }
            else
            {
                if (eventModel!!.parent != null)
                {
                    mBinding.tvTeamMember.text = eventModel!!.resource.name
                }
                else{
                    mBinding.tvTeamMember.text = "Select Contact"
                }

            }

        }


    }

}