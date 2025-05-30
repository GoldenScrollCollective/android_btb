package org.lemonadestand.btb.components.dialog

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.App
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseDialogFragment
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.core.manager.PostsManager
import org.lemonadestand.btb.core.manager.UserManager
import org.lemonadestand.btb.extensions.setOnSingleClickListener
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class DeletePostDialogFragment @JvmOverloads constructor(
	fragment: BaseFragment,
	val uniqueId: String,
	val email: String,
	val onDeleted: () -> Unit
) : BaseDialogFragment(fragment, R.layout.layout_delete_post) {
	private lateinit var codeView: EditText
	private lateinit var loadingView: LinearLayout
	private lateinit var loadedView: LinearLayout
	private lateinit var statusView: TextView
	private lateinit var btnResend: TextView

	private var resendLimit = 60

	override fun init() {
		super.init()

		isCancelable = false
		dialog?.window?.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)

		codeView = rootView.findViewById(R.id.codeView)
		loadingView = rootView.findViewById(R.id.loadingView)
		loadedView = rootView.findViewById(R.id.loadedView)
		statusView = rootView.findViewById(R.id.statusView)

		btnResend = rootView.findViewById(R.id.btnResend)
		btnResend.setOnSingleClickListener { handleSendCode() }

		val btnCancel = rootView.findViewById<TextView>(R.id.btnCancel)
		btnCancel.setOnSingleClickListener { dismiss() }

		val btnConfirm = rootView.findViewById<TextView>(R.id.btnConfirm)
		btnConfirm.setOnSingleClickListener { handleConfirm() }
	}

	override fun update() {
		super.update()

		handleSendCode()
	}

	private fun startResendLimitTimer() {
		resendLimit = 60
		val timer = Timer()
		timer.scheduleAtFixedRate(0, 1000) {
			resendLimit--
			if (resendLimit <= 0) {
				CoroutineScope(Dispatchers.Main).launch {
					btnResend.text = "Resend"
					btnResend.isClickable = true
				}

				timer.cancel()
				return@scheduleAtFixedRate
			}

			CoroutineScope(Dispatchers.Main).launch {
				btnResend.text = "Resend in ${resendLimit}s"
				btnResend.isClickable = false
			}
		}
	}

	private fun handleSendCode() {
		loadingView.visibility = View.VISIBLE
		loadedView.visibility = View.GONE

		UserManager.get2FACode(email) { success ->
			loadingView.visibility = View.GONE
			loadedView.visibility = View.VISIBLE

			if (!success) {
				statusView.text = "Something went wrong and please try again."
			} else {
				statusView.text = "We sent code to your email."

				startResendLimitTimer()
			}
		}
	}

	private fun handleConfirm() {
		val code = codeView.text.toString()
		if (code.length < 6) {
			Toast.makeText(App.instance, "Invalid 2FA code", Toast.LENGTH_LONG).show()
			return
		}

		PostsManager.deletePost(uniqueId, code) {
			onDeleted.invoke()
			dismiss()
		}
	}
}