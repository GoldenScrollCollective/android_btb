package org.lemonadestand.btb.features.dashboard.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.FilterButton
import org.lemonadestand.btb.singleton.Filter

class FilterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0,
    val filter: String
): LinearLayout(context, attrs, defStyle, defStyleRes) {
    companion object {
        val TAG: String = FilterView::class.java.simpleName
    }

    var onSelect: ((value: String) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_dashboard_filter, this, true)

        val filters = arrayListOf(Filter.PUBLIC, Filter.RESOURCE, Filter.MINE, Filter.ORGANIZATION)
        val btnFilters: ArrayList<FilterButton> = arrayListOf(
            findViewById(R.id.btnPublic),
            findViewById(R.id.btnResource),
            findViewById(R.id.btnMine),
            findViewById(R.id.btnOrganization)
        )

        for (btnFilter in btnFilters) {
            val index = btnFilters.indexOf(btnFilter)
            btnFilter.isSelected = filter === filters[index]
            btnFilter.setOnClickListener {
                for (btn in btnFilters) {
                    btn.isSelected = false
                }
                btnFilter.isSelected = true
                onSelect?.invoke(filters[index])
            }
        }
    }
}