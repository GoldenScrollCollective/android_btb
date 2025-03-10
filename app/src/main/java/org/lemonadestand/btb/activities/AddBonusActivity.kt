package org.lemonadestand.btb.activities

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.BonusStepper
import org.lemonadestand.btb.components.NavHeaderView
import org.lemonadestand.btb.components.base.BaseActivity
import org.lemonadestand.btb.components.radio.RadioButton
import org.lemonadestand.btb.components.radio.RadioGroup
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.features.common.fragments.WriteMessageFragment
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.mvvm.factory.CommonViewModelFactory
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import org.lemonadestand.btb.mvvm.viewmodel.HomeViewModel
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar


class AddBonusActivity : BaseActivity(R.layout.activity_add_bonus) {
	private lateinit var messageView: TextView
	private lateinit var amountView: TextView
	private lateinit var totalAmountView: TextView

	private lateinit var btnGiving: RadioButton
	private lateinit var btnSpending: RadioButton

	private lateinit var bonusStepper: BonusStepper

	private var debit = Post.Debit.GIVE
		set(value) {
			field = value
			handleDebit()
		}
	private var amountGive = 0
		set(value) {
			field = value
			if (this::amountView.isInitialized && this.debit == Post.Debit.GIVE) amountView.text = "\$${value}"
		}
	private var amountSpend = 0
		set(value) {
			field = value
			if (this::amountView.isInitialized && this.debit == Post.Debit.SPEND) amountView.text = "\$${value}"
		}
	private var htmlMessage = ""
		set(value) {
			field = value
			if (this::messageView.isInitialized) messageView.text = HtmlCompat.fromHtml(value, HtmlCompat.FROM_HTML_MODE_LEGACY)
		}
	var data = arrayListOf<String>()
	var currentUser: User? = null
	var currentDate = ""

	lateinit var viewModel: HomeViewModel
	var calendar: Calendar? = null

	private var post: Post? = null

	override fun init() {
		super.init()

		messageView = findViewById(R.id.messageView)
		amountView = findViewById(R.id.amountView)
		totalAmountView = findViewById(R.id.totalAmountView)

		btnGiving = findViewById(R.id.btnGiving)
		btnSpending = findViewById(R.id.btnSpending)

		bonusStepper = findViewById(R.id.bonusStepper)
		bonusStepper.onChange = { value ->
			if (debit == Post.Debit.GIVE) {
				amountGive = value.toInt()
				totalAmountView.text = buildString {
					append("\$${amountGive * post!!.users.size}")
				}
			} else {
				amountSpend = value.toInt()
				totalAmountView.text = buildString {
					append("\$${amountSpend * post!!.users.size}")
				}
			}
		}

		post = intent.getParcelableExtra("post")

		val navHeaderView = findViewById<NavHeaderView>(R.id.navHeaderView)
		navHeaderView.onLeftPressed = { onBackPressedDispatcher.onBackPressed() }
		navHeaderView.onRightPressed = { handleSave() }

		val debitRadioGroup = findViewById<RadioGroup>(R.id.debitRadioGroup)
		debitRadioGroup.onSelect = {
			debit = if (it == 0) {
				Post.Debit.GIVE
			} else {
				Post.Debit.SPEND
			}
		}

		getData()
		setClickEvents()
		setUpViewModel()
	}

	override fun update() {
		super.update()

		handleDebit()
	}

	private fun getData() {
		currentUser = Utils.getUser(this) ?: return
		calendar = Calendar.getInstance()
		val dateFormat1 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		val formattedDate1 = dateFormat1.format(calendar!!.time)
		currentDate = formattedDate1

		btnGiving.text = buildString {
			append("Giving (\$${currentUser!!.give.toDouble()})")
		}
		btnSpending.text = buildString {
			append("Spending (\$${currentUser!!.spend.toDouble()})")
		}
	}

	private fun setClickEvents() {
		val btnMessage = findViewById<LinearLayout>(R.id.btnMessage)
		btnMessage.setOnClickListener {
			WriteMessageFragment().apply {
				arguments = Bundle().apply { putString("message", htmlMessage) }
				onDone = {
					htmlMessage = it
				}
				show(supportFragmentManager, tag)
			}
		}
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

	private fun handleDebit() {
		post ?: return

		if (debit == Post.Debit.GIVE) {
			bonusStepper.min = 0.0
			bonusStepper.max = currentUser!!.give.toDouble() / post!!.users.size
			amountView.text = "\$${amountGive}"
		} else {
			bonusStepper.min = 0.0
			bonusStepper.max = currentUser!!.spend.toDouble() / post!!.users.size
			amountView.text = "\$${amountSpend}"
		}
	}

	private fun handleSave() {
		if (htmlMessage.isEmpty() || htmlMessage == "<p><br></p>") {
			Toast.makeText(this, "Please leave a message", Toast.LENGTH_LONG).show()
			return
		}

		val requestBody = ShareStoryBody(
			by_user_id = "user/${currentUser!!.uniqueId}",
			parent_id = post?.uniqueId,
			resource = if (post?.type == "comment") post?.user?.id else "",
			bonus = if (debit == Post.Debit.GIVE) "$amountGive" else "$amountSpend",
			debit = debit.value,
			type = post?.type,
			html = htmlMessage,
			users = post?.users?.map { x -> x.id }?.let { ArrayList(it) }
		)
		viewModel.shareStory(requestBody)
	}
}