package org.lemonadestand.btb.components.base

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment

abstract class BaseDialogFragment(
	val fragment: BaseFragment,
	@LayoutRes val layoutId: Int
) : AppCompatDialogFragment() {
	companion object {
		val TAG = this::class.java.name
	}

	protected lateinit var rootView: View

	var onDismiss: (() -> Unit)? = null

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		rootView = inflater.inflate(layoutId, container, false)
		init()
		return rootView
	}

	override fun onStart() {
		super.onStart()
		update()
	}

	protected open fun init() {}

	open fun update() {}

	override fun dismiss() {
		super.dismiss()
		onDismiss?.invoke()
	}

	fun show() {
		super.show(fragment.childFragmentManager, TAG)
	}

	fun setWidthPercent(percentage: Int) {
		val percent = percentage.toFloat() / 100
		val dm = Resources.getSystem().displayMetrics
		val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
		val percentWidth = rect.width() * percent
		dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
	}
}