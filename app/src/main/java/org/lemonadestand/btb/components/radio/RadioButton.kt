package org.lemonadestand.btb.components.radio

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import org.lemonadestand.btb.R

class RadioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {
    companion object {
        val TAG: String = this::class.java.simpleName
    }

    init {
        attrs?.let { it ->
            val attributes = context.obtainStyledAttributes(it, R.styleable.RadioButton)

            isSelected = attributes.getBoolean(R.styleable.RadioButton_selected, false)

            attributes.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handleSelected()
    }

    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        handleSelected()
    }

    private fun handleSelected() {
        if (isSelected) {
            setBackgroundResource(R.drawable.back_for_all)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            elevation = 5f
        } else {
            setBackgroundResource(0)
            setTextColor(ContextCompat.getColor(context, R.color.light_grey_ios))
            elevation = 0f
        }
    }
}
