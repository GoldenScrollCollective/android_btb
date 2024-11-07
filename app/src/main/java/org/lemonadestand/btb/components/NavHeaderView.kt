package org.lemonadestand.btb.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.extenstions.setOnSingleClickListener

class NavHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
): RelativeLayout(context, attrs, defStyle, defStyleRes) {
    companion object {
        val TAG: String = NavHeaderView::class.java.simpleName
    }

    var onBackPressed: (() -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_nav_header, this, true)

        attrs?.let { it ->
            val attributes = context.obtainStyledAttributes(it, R.styleable.NavHeaderView)

            val titleView = findViewById<TextView>(R.id.titleView)
            titleView.text = attributes.getString(R.styleable.NavHeaderView_title)

            val btnBack = findViewById<ImageView>(R.id.btnBack)
            btnBack.setOnSingleClickListener { onBackPressed?.invoke() }

            attributes.recycle()
        }
    }
}