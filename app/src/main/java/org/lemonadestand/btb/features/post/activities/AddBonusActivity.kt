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
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.ClickType
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.databinding.ActivityAddBonusBinding
import org.lemonadestand.btb.features.common.fragments.UserListFragment
import org.lemonadestand.btb.features.common.fragments.WriteMessageFragment
import org.lemonadestand.btb.features.common.models.UserListModel
import org.lemonadestand.btb.features.common.models.body.AppreciationRequestBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.interfaces.OnItemClickListener
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar


class AddBonusActivity : AppCompatActivity(), OnItemClickListener {
	private lateinit var mBinding: ActivityAddBonusBinding
	private var bottomSheetFragment: UserListFragment? = null

	private var whyThank: String = ""
	private var amount = 0
	private var amountSpend = 0
	private var htmlMessage = ""
		set(value) {
			field = value
			if (this::mBinding.isInitialized) mBinding.txtMessage.text = HtmlCompat.fromHtml(value, HtmlCompat.FROM_HTML_MODE_LEGACY)
		}
	private var isGivingSelected = true
	var data = arrayListOf<String>()
	var currentUser: User? = null
	var currentDate = ""
	var selectedUser: UserListModel? = null

	var maxValue = 0.0
	var maxValueSpend = 0.0
	lateinit var appreciationRequestBody: AppreciationRequestBody
	lateinit var viewModel: HomeViewModel
	var calendar: Calendar? = null

	var debit = "give"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mBinding = ActivityAddBonusBinding.inflate(layoutInflater)
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
			maxValue = currentUser!!.give.toDouble()
			mBinding.btnGiving.text = buildString {
				append(" Giving (\$${maxValue}) ")
			}
		}
		maxValueSpend = currentUser!!.spend.toDouble()
		mBinding.btnSpending.text = buildString {
			append(" Spending (\$${maxValueSpend}) ")
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

	private fun setClickEvents() {
		mBinding.icBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

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
			showBottomSheet()
		}

		mBinding.cnThanks.setOnClickListener {
			mBinding.spinner.performClick()
		}

		mBinding.btnIncrease.setOnClickListener {

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

		}

		mBinding.btnGiving.setOnClickListener {
			debit = "give"
			mBinding.amount.text = buildString {
				append("\$${amount}")
			}
			updateGivingButtonUi(true)
		}
		mBinding.btnSpending.setOnClickListener {

			debit = "spend"
			mBinding.amount.text = buildString {
				append("\$${amountSpend}")
			}
			updateGivingButtonUi(false)
		}

		mBinding.btnSave.setOnClickListener {


//            if (selectedUser == null) {
//                Toast.makeText(
//                    this,
//                    "Please select what would you like to thank!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return@setOnClickListener
//            }

//            appReciationBody = AppReciationBody(
//                uniq_id = "",
//                resource = "user/${selectedUser!!.uniq_id}",
//                html = htmlMessage,
//                title = whyThank,
//                created = currentDate,
//                parent_id = "",
//                by_user_id = "user/${currentUser!!.uniqId}",
//                modified = currentDate,
//                type = "comment",
//                visibility = if (mBinding.switchIsPrivate.isChecked) "private" else "public",
//                user = ShareStoryUser(
//                    id = "user/${selectedUser!!.uniq_id}",
//                    name = currentUser!!.name,
//                    picture = currentUser!!.picture
//                ),
//                meta = AppReciationMeta(bonus = "$amount", debit = debit)
//            )
//            viewModel.addAppreciation(appReciationBody)

			val requestBody = ShareStoryBody(
				uniq_id = "",
				resource = "user/${currentUser!!.uniqueId}",
				html = htmlMessage,
				created = "",
				parent_id = "",
				modified = "",
				by_user_id = "",
				visibility = if (mBinding.switchIsPrivate.isChecked) "private" else "public",
				user = ShareStoryUser(id = "", name = "")
			)
			viewModel.shareStory(requestBody)
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
			bottomSheetFragment = UserListFragment()
			bottomSheetFragment?.setCallback(this)
			bottomSheetFragment?.show(fragmentManager, bottomSheetFragment!!.tag)

		} else {
			bottomSheetFragment!!.show(supportFragmentManager, bottomSheetFragment!!.tag)
		}

	}

	override fun onItemClicked(`object`: Any?, index: Int, type: ClickType, superIndex: Int) {
		bottomSheetFragment?.dismiss()
		val userModel = `object` as UserListModel
		selectedUser = userModel
		mBinding.tvUserName.text = userModel.name
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