package org.lemonadestand.btb.components.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BaseBottomSheetDialogFragment(
    val fragment: BaseFragment,
    @LayoutRes val layoutId: Int
): BottomSheetDialogFragment() {
    companion object {
        val TAG: String = this::class.java.name
    }

    protected lateinit var rootView: View
    var onDismiss: (() -> Unit) ? = null

    protected val mainCoroutineScope = CoroutineScope(Dispatchers.Main)
    protected val bgCoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(layoutId, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onStart() {
        super.onStart()
        update()
    }

    protected open fun init() {}

    open fun update() {}

    fun show() {
        super.show(fragment.childFragmentManager, TAG)
    }

    override fun dismiss() {
        super.dismiss()
        onDismiss?.invoke()
    }
}