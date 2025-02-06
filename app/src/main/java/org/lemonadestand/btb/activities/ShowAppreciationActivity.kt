package org.lemonadestand.btb.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.google.gson.Gson
import org.lemonadestand.btb.App
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.MediaPreviewView
import org.lemonadestand.btb.components.UploadButton
import org.lemonadestand.btb.components.base.BaseActivity
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.manager.PostsManager
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.core.response.ShareStoryResponse
import org.lemonadestand.btb.databinding.ActivityShowAppreciationBinding
import org.lemonadestand.btb.extensions.lastPathComponent
import org.lemonadestand.btb.features.common.fragments.SelectMultiUsersBottomSheetFragment
import org.lemonadestand.btb.features.common.fragments.WriteMessageFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.AppreciationMeta
import org.lemonadestand.btb.features.common.models.body.AppreciationRequestBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.AWSUploadHelper
import org.lemonadestand.btb.utils.Utils
import java.util.Calendar


class ShowAppreciationActivity : BaseActivity(R.layout.activity_show_appreciation),
	OnItemClickListener {
	private lateinit var mBinding: ActivityShowAppreciationBinding
	private var bottomSheetFragment: SelectMultiUsersBottomSheetFragment? = null

	private var whyThank: String = ""
	private var bonusAmount = 0
	private var amountSpend = 0
	private var totalAmount = 0
	private var htmlMessage = ""
		set(value) {
			field = value
			if (this::mBinding.isInitialized) mBinding.txtMessage.text =
				HtmlCompat.fromHtml(value, HtmlCompat.FROM_HTML_MODE_LEGACY)
		}

	private var isGivingSelected = true
	var data = arrayListOf<String>()
	var currentUser: User? = null
	var selectedUser: UserListModel? = null

	var maxValue = 0.0
	var maxValueSpend = 0.0

	var calendar: Calendar? = null

	var debit = "give"

	private var reminderUerList: ArrayList<UserListModel> = ArrayList()
	private var reminderUerListJson: ArrayList<String> = ArrayList()

	private lateinit var mediaPreviewView: MediaPreviewView
	private var uploadedFileUrl: String? = null
		set(value) {
			field = value
			if (this::mediaPreviewView.isInitialized) {
				if (value == null) {
					mediaPreviewView.visibility = View.GONE
					return
				}
				mediaPreviewView.visibility = View.VISIBLE
				mediaPreviewView.url = value
			}
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mBinding = ActivityShowAppreciationBinding.inflate(layoutInflater)

		setContentView(mBinding.root)
		setBackgrounds()
		setClickEvents()
		setSpinner()
		setUpViewModel()

		mBinding.navHeaderView.onLeftPressed = { handleCancel() }
		mBinding.navHeaderView.onRightPressed = { handleSave() }

		mediaPreviewView = findViewById(R.id.mediaPreviewView)
		val uploadButton = findViewById<UploadButton>(R.id.uploadButton)
		uploadButton.onUploaded = { uploadedFileUrl = it }
	}

	override fun update() {
		super.update()

		currentUser = Utils.getUser(this)

		if (currentUser!!.give != null) {
			maxValue = currentUser!!.give.toDouble()
//            maxValue = 10.0
			mBinding.btnGiving.text = buildString {
				append(" Giving (\$${maxValue}) ")
			}
		}
		if (currentUser!!.spend != null) {
			maxValueSpend = currentUser!!.spend.toDouble()
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

		mBinding.spinner.adapter =
			ArrayAdapter(this, R.layout.row_dropdown_item, R.id.tv_item_name, data)
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
			totalAmount = bonusAmount * reminderUerList.size
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
		mBinding.btnMessage.setOnClickListener {
			WriteMessageFragment().apply {
				arguments = Bundle().apply { putString("message", htmlMessage) }
				onDone = {
					htmlMessage = it
				}
				show(supportFragmentManager, tag)
			}
		}

		mBinding.selectUser.setOnClickListener {
			SelectMultiUsersBottomSheetFragment().apply {
				arguments = Bundle().apply {
					putString("title", "Select team members")
					putString("list", Gson().toJson(reminderUerList))
				}
				setCallback(this@ShowAppreciationActivity)
				show(supportFragmentManager, tag)
			}
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
				if (bonusAmount == maxValue.toInt()) {
					return@setOnClickListener
				}
				bonusAmount += 1
				mBinding.amount.text = buildString {
					append("$")
					append(bonusAmount)
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
				if (bonusAmount > 0) {
					bonusAmount -= 1
				}

				mBinding.amount.text = buildString {
					append("$")
					append(bonusAmount)
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
				append("\$${bonusAmount}")
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

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
		bottomSheetFragment?.dismiss()

		val list = `object` as ArrayList<UserListModel>?
		val textString: String

		if (list.isNullOrEmpty()) {
			reminderUerList = ArrayList()
			reminderUerListJson = ArrayList()
			textString = getString(R.string.set_notification)
		} else {
			selectedUser = list[0]
			reminderUerList = list
			textString = reminderUerList.joinToString(separator = ", ") { it.name }
			reminderUerListJson.clear()

			for (i in 0 until reminderUerList.size) {
				reminderUerListJson.add(reminderUerList[i].uniqueId)
			}

			Log.e("ReminderUserListJson", reminderUerListJson.toString())
			if (reminderUerListJson.size > 1) {
				mBinding.linearLayout9.visibility = View.VISIBLE
			} else {
				mBinding.linearLayout9.visibility = View.GONE
			}
		}

		if (bonusAmount > 0) {
			totalAmount = bonusAmount * reminderUerListJson.size

			mBinding.totalDebit.text = buildString {
				append("$")
				append(totalAmount)
			}
		}
		mBinding.tvUserName.text = (textString)

	}

	private fun setUpViewModel() {
		PostsManager.error.observe(this) {
			Singleton.handleResponse(response = it, this, "ReplyCommentActivity")
			ProgressDialogUtil.dismissProgressDialog()
		}
		PostsManager.isLoading.observe(this) {
			Log.e("value==>", it.toString())
			if (it) {
				ProgressDialogUtil.showProgressDialog(this)
			} else {
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
		PostsManager.noInternet.observe(this) {
			Toast.makeText(this, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
		}
	}

	private fun handleSave() {
		val currentUser = Utils.getUser(App.instance) ?: return
		if (selectedUser == null) {
			Toast.makeText(this, "Please select what would you like to thank!", Toast.LENGTH_SHORT)
				.show()
			return
		}
		if (whyThank.isEmpty()) {
			Toast.makeText(this, "Please select why you're saying thanks.", Toast.LENGTH_SHORT)
				.show()
			return
		}

		val requestBody = AppreciationRequestBody(
			by_user_id = "user/${currentUser.uniqueId}",
			uniq_id = "",
			resource = "user/${selectedUser!!.uniqueId}",
			html = htmlMessage,
			title = whyThank,
			media = uploadedFileUrl?.lastPathComponent(),
			parent_id = "",
			type = "comment",
//                visibility = if (mBinding.switchIsPrivate.isChecked) "private" else "public",
			visibility = "public",
			user = ShareStoryUser(
				id = "user/${selectedUser!!.uniqueId}",
				name = currentUser.name,
				picture = currentUser.picture
			),
			meta = AppreciationMeta(bonus = "$totalAmount", debit = debit),
			users = reminderUerListJson
		)
		PostsManager.addAppreciation(requestBody) { response ->
			handleCommonResponse(this, response.body() as ShareStoryResponse)
			ProgressDialogUtil.dismissProgressDialog()

			if (!response.isSuccessful) {
				return@addAppreciation
			}

			finish()
		}
	}

	private fun handleCancel() {
		AWSUploadHelper.delete(uploadedFileUrl)
		onBackPressedDispatcher.onBackPressed()
	}
}