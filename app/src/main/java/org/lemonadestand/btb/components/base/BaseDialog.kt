package org.lemonadestand.btb.components.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.annotation.LayoutRes

abstract class BaseDialog(context: Context, @LayoutRes val layoutResId: Int): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(layoutResId)

        init()
    }

    protected open fun init() {}

    open fun update() {}
}