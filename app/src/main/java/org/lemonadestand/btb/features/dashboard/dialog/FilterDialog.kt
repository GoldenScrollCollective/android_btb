package org.lemonadestand.btb.features.dashboard.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.FilterButton
import org.lemonadestand.btb.components.base.BaseDialog
import org.lemonadestand.btb.singleton.Filter


class FilterDialog @JvmOverloads constructor(
    val anchorView: View,
    context: Context
): BaseDialog(context, R.layout.layout_dashboard_filter) {
    var onSelect: ((value: String) -> Unit)? = null

    override fun init() {
        super.init()

        setCancelable(true)

        window?.attributes?.apply {
            gravity = Gravity.TOP
//            x = 100 //x position
            y = 100 //y position
        }
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnDefault: FilterButton = findViewById(R.id.btnPublic)
        btnDefault.setOnClickListener {
            onSelect?.invoke(Filter.PUBLIC)
            dismiss()
        }

        val btnResource: FilterButton = findViewById(R.id.btnResource)
        btnResource.setOnClickListener {
            onSelect?.invoke(Filter.RESOURCE)
            dismiss()
        }

        val btnMine: FilterButton = findViewById(R.id.btnMine)
        btnMine.setOnClickListener {
            onSelect?.invoke(Filter.MINE)
            dismiss()
        }

        val btnOrganization: FilterButton = findViewById(R.id.btnOrganization)
        btnOrganization.setOnClickListener {
            onSelect?.invoke(Filter.ORGANIZATION)
            dismiss()
        }
    }
}