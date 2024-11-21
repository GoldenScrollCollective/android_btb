package org.lemonadestand.btb.features.common.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.lemonadestand.btb.databinding.FragmentWriteMessageBinding
import org.lemonadestand.btb.extensions.setOnSingleClickListener


class WriteMessageFragment : BottomSheetDialogFragment() {

	var onDone: ((value: String) -> Unit)? = null

	lateinit var mBinding: FragmentWriteMessageBinding
	private var isSingleLine = false
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mBinding = FragmentWriteMessageBinding.inflate(
			LayoutInflater.from(inflater.context),
			container,
			false
		)

		resolveArguments()

		mBinding.btnDone.setOnSingleClickListener {
			mBinding.htmlEditor.onReceivedHtml = {
				onDone?.invoke(it)
				dismiss()
			}
			mBinding.htmlEditor.requestHtml()
		}

		return mBinding.root
	}

	private fun resolveArguments() {
		arguments?.let {
			if (it.containsKey("title")) {
				mBinding.titleView.text = it.getString("title")
			}

			val message = it.getString("message") ?: ""
			mBinding.htmlEditor.value = message

			if (it.containsKey("is_single_line")) {
				isSingleLine = it.getBoolean("is_single_line", false)
			}
		}
	}
}