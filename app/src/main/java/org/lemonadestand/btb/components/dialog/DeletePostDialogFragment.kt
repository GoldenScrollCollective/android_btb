package org.lemonadestand.btb.components.dialog

import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.lemonadestand.btb.App
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseDialogFragment
import org.lemonadestand.btb.components.base.BaseFragment
import org.lemonadestand.btb.core.manager.PostsManager
import org.lemonadestand.btb.core.manager.UserManager
import org.lemonadestand.btb.extensions.setOnSingleClickListener

class DeletePostDialogFragment @JvmOverloads constructor(
	fragment: BaseFragment,
	val uniqueId: String,
	val email: String,
	val onDeleted: () -> Unit
) : BaseDialogFragment(fragment, R.layout.layout_delete_post) {
	private lateinit var codeView: EditText

	override fun init() {
		super.init()

		codeView = rootView.findViewById(R.id.codeView)

		val btnCancel = rootView.findViewById<TextView>(R.id.btnCancel)
		btnCancel.setOnSingleClickListener { dismiss() }

		val btnConfirm = rootView.findViewById<TextView>(R.id.btnConfirm)
		btnConfirm.setOnSingleClickListener { handleConfirm() }

		UserManager.get2FACode(email) { success ->
			if (!success) {
				dismiss()
			}
		}
	}

	private fun handleConfirm() {
		val code = codeView.text.toString()
		if (code.length < 6) {
			Toast.makeText(App.instance, "Invalid 2FA code", Toast.LENGTH_LONG).show()
		}

		PostsManager.deletePost(uniqueId, code) {
			onDeleted.invoke()
		}
	}
}