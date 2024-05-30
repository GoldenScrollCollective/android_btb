package org.lemonadestand.btb.features.post.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.databinding.ActivityShowAppreciationBinding
import org.lemonadestand.btb.features.common.fragments.UserListFragmentMulti
import org.lemonadestand.btb.features.common.fragments.WriteMessageFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.AppReciationBody
import org.lemonadestand.btb.features.common.models.body.AppReciationMeta
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.models.User
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar


class ShowAppreciationActivity : AppCompatActivity(), OnItemClickListener {
	private lateinit var mBinding: ActivityShowAppreciationBinding
	private var bottomSheetFragment: UserListFragmentMulti? = null
	private var bottomSheetFragmentMessage: WriteMessageFragment? = null
	private var whyThank: String = ""
	private var amount = 0
	private var amountSpend = 0
	private var totalAmount = 0
	var htmlMessage = ""
	private var isGivingSelected = true
	var data = arrayListOf<String>()
	var currentUser: User? = null
	var currentDate = ""
	var selectedUser: UserListModel? = null

	var maxValue = 0.0
	var maxValueSpend = 0.0
	lateinit var appReciationBody: AppReciationBody
	lateinit var viewModel: HomeViewModel
	var calendar: Calendar? = null

	var debit = "give"

	private var reminderUerList: ArrayList<UserListModel> = ArrayList()
	private var reminderUerListJson: ArrayList<String> = ArrayList()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mBinding = ActivityShowAppreciationBinding.inflate(layoutInflater)
		setContentView(mBinding.root)
		getData()
		setBackgrounds()
		setClickEvents()
		setSpinner()
		setUpViewModel()
	}

	private fun getData() {
		currentUser = Utils.getUser(this)
		calendar = Calendar.getInstance()
		val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		val formattedDate1 = dateFormat1.format(calendar!!.time)
		currentDate = formattedDate1
		if (currentUser!!.give != null) {
			maxValue = currentUser!!.give!!.toDouble()
//            maxValue = 10.0
			mBinding.btnGiving.text = buildString {
				append(" Giving (\$${maxValue}) ")
			}
		}
		if (currentUser!!.spend != null) {
			maxValueSpend = currentUser!!.spend!!.toDouble()
			mBinding.btnSpending.text = buildString {
				append(" Spending (\$${maxValueSpend}) ")
			}
		}

	}

	private fun setBackgrounds() {
		mBinding.btnGiving.setBackgroundResource(R.drawable.back_for_all)
	}

	private fun setSpinner() {
		data = arrayListOf(
			"Just Because",
			"Good Work",
			"Caring",
			"Hard Work",
			"Kindness",
			"Follow Through",
			"Going Extra Mile",
			"Smart Thinking",
			"Positive Attitude",
			"Helping Hands",
			"Generosity"
		)

		//mBinding.spinner.adapter = ArrayAdapter(this,R.layout.row_dropdown_item, android.R.layout.simple_list_item_1, data)
		mBinding.spinner.adapter =
			ArrayAdapter(this, R.layout.row_dropdown_item, R.id.tv_item_name, data);

		mBinding.spinner.onItemSelectedListener = object :
			AdapterView.OnItemSelectedListener {
			override fun onItemSelected(
				parent: AdapterView<*>?,
				view: View,
				position: Int,
				id: Long
			) {
				whyThank = data[position]
				mBinding.thanksReason.text = whyThank
				Log.e("spinner=>", whyThank)
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {}
		}
	}

	private fun setTotalMoney(type: String) {
		if (type == "give") {
			totalAmount = amount * reminderUerList.size
			Log.i("TotalAmount====>", totalAmount.toString())

			mBinding.totalDebit.text = buildString {
				append("$")
				append(totalAmount)
			}
		} else {
			totalAmount = amountSpend * reminderUerList.size

			mBinding.totalDebit.text = buildString {
				append("$")
				append(totalAmount)
			}
		}
	}

	private fun setClickEvents() {
		mBinding.icBack.setOnClickListener { onBackPressed() }


		mBinding.btnMessage.setOnClickListener {
			showBottomSheetMessage()
		}

		mBinding.selectUser.setOnClickListener {
			showBottomSheet()
		}

		mBinding.cnThanks.setOnClickListener {
			mBinding.spinner.performClick()
		}

		mBinding.btnIncrease.setOnClickListener {
			currentUser = Utils.getUser(this)
			Log.e("Money", maxValue.toString())
			maxValue = currentUser!!.give.toDouble()
//            maxValue = 10.0
			maxValueSpend = currentUser!!.spend.toDouble()
			if (debit == "give") {
				if (amount == maxValue.toInt()) {
					return@setOnClickListener
				}
				amount += 1
				mBinding.amount.text = buildString {
					append("$")
					append(amount)
				}
			} else {
				if (amountSpend == maxValueSpend.toInt()) {
					return@setOnClickListener
				}
				amountSpend += 1
				mBinding.amount.text = buildString {
					append("$")
					append(amountSpend)
				}
			}

			setTotalMoney(debit)
		}
		mBinding.btnDecrease.setOnClickListener {
			if (debit == "give") {
				if (amount > 0) {
					amount -= 1
				}

				mBinding.amount.text = buildString {
					append("$")
					append(amount)
				}
			} else {
				if (amountSpend > 0) {
					amountSpend -= 1
				}
				mBinding.amount.text = buildString {
					append("$")
					append(amountSpend)
				}
			}
			setTotalMoney(debit)
		}

		mBinding.btnGiving.setOnClickListener {
			debit = "give"
			mBinding.amount.text = buildString {
				append("\$${amount}")
			}
			updateGivingButtonUi(true)

			setTotalMoney(debit)
		}
		mBinding.btnSpending.setOnClickListener {

			debit = "spend"
			mBinding.amount.text = buildString {
				append("\$${amountSpend}")
			}
			updateGivingButtonUi(false)

			setTotalMoney(debit)
		}

		mBinding.btnSave.setOnClickListener {
			if (selectedUser == null) {
				Toast.makeText(
					this,
					"Please select what would you like to thank!",
					Toast.LENGTH_SHORT
				).show()
				return@setOnClickListener
			}

			appReciationBody = AppReciationBody(
				uniq_id = "",
				resource = "user/${selectedUser!!.uniq_id}",
				html = htmlMessage,
				title = whyThank,
				created = currentDate,
				parent_id = "",
				by_user_id = "user/${currentUser!!.uniqueId}",
				modified = currentDate,
				type = "comment",
				visibility = if (mBinding.switchIsPrivate.isChecked) "private" else "public",
				user = ShareStoryUser(
					id = "user/${selectedUser!!.uniq_id}",
					name = currentUser!!.name,
					picture = currentUser!!.picture
				),
				meta = AppReciationMeta(bonus = "$totalAmount", debit = debit),
				users = reminderUerListJson
			)
			viewModel.addAppreciation(appReciationBody)
		}
	}

	private fun updateGivingButtonUi(isGiving: Boolean) {
		isGivingSelected = isGiving
		if (isGiving) {
			mBinding.btnGiving.setBackgroundResource(R.drawable.back_for_all)
			mBinding.btnSpending.setBackgroundResource(0)
		} else {
			mBinding.btnGiving.setBackgroundResource(0)
			mBinding.btnSpending.setBackgroundResource(R.drawable.back_for_all)
		}
	}


	private fun showBottomSheet() {
		if (bottomSheetFragment == null) {
			val fragmentManager: FragmentManager = supportFragmentManager
//            bottomSheetFragment = UserListFragment()
			val fragment = UserListFragmentMulti()

			val bundle = Bundle()
			Log.e("Multi selector json from users=>", reminderUerList.toString())
			val json = Gson().toJson(reminderUerList)
			Log.i("Multi selector json from activity=>", json.toString())
			bundle.putString("list", json)

			fragment.arguments = bundle

			bottomSheetFragment = fragment

			bottomSheetFragment?.setCallback(this)
			bottomSheetFragment?.show(fragmentManager, bottomSheetFragment!!.tag)

		} else {
			Log.e("BottomSheet===>", reminderUerListJson.toString())
			val fragment = UserListFragmentMulti()

			val bundle = Bundle()
			val json = Gson().toJson(reminderUerList)
			bundle.putString("list", json)
			fragment.arguments = bundle

			bottomSheetFragment = fragment

			bottomSheetFragment?.setCallback(this)

			bottomSheetFragment!!.show(supportFragmentManager, bottomSheetFragment!!.tag)
		}
	}

	private fun showBottomSheetMessage() {

		val fragmentManager: FragmentManager = supportFragmentManager
		bottomSheetFragmentMessage = WriteMessageFragment()

		bottomSheetFragmentMessage?.setCallback(this)
		if (mBinding.txtMessage.text != getString(R.string.write_your_message_here)) {
			Log.e("htmlMessage=>", htmlMessage)
			val args = Bundle()
			args.putString("message", htmlMessage)
			bottomSheetFragmentMessage?.arguments = args
		}
		bottomSheetFragmentMessage?.show(fragmentManager, bottomSheetFragmentMessage!!.tag)


	}

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
		if (index == -1) {
			val message = `object`.toString()
			htmlMessage = message
			if (message != getString(R.string.write_your_message_here)) {

				mBinding.txtMessage.text =
					HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)
			}

			return
		}
		bottomSheetFragment?.dismiss()

		val list = `object` as ArrayList<UserListModel>
		val textString: String

		selectedUser = list[0]

		if (list.isNullOrEmpty()) {
			reminderUerList = ArrayList()
			reminderUerListJson = ArrayList()
			textString = getString(R.string.set_notification)

		} else {
			reminderUerList = list
			textString = reminderUerList.joinToString(separator = ", ") { it.name }
			reminderUerListJson.clear()

			for (i in 0 until reminderUerList.size) {
				reminderUerListJson.add(reminderUerList[i].uniq_id)
			}

			Log.e("ReminderUserListJson", reminderUerListJson.toString())
			if (reminderUerListJson.size > 1) {
				mBinding.linearLayout9.visibility = View.VISIBLE
			} else {
				mBinding.linearLayout9.visibility = View.GONE
			}
		}

		if (amount > 0) {
			totalAmount = amount * reminderUerListJson.size

			mBinding.totalDebit.text = buildString {
				append("$")
				append(totalAmount)
			}
		}
		mBinding.tvUserName.text = (textString)

	}

	private fun setUpViewModel() {

		val repository = HomeRepository()
		val viewModelProviderFactory =
			CommonViewModelFactory((this).application, repository)
		viewModel = ViewModelProvider(this, viewModelProviderFactory)[HomeViewModel::class.java]


		viewModel.liveError.observe(this) {
			Singleton.handleResponse(response = it, this, "ReplyCommentActivity")
			ProgressDialogUtil.dismissProgressDialog()
		}

		viewModel.commonResponse.observe(this) {
			handleCommonResponse(this, it)
			ProgressDialogUtil.dismissProgressDialog()
			finish()
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