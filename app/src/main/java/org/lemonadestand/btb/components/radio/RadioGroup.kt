package org.lemonadestand.btb.components.radio

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import org.lemonadestand.btb.R
import org.lemonadestand.btb.extensions.setOnSingleClickListener

class RadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {
    companion object {
        val TAG: String = this::class.java.simpleName
    }

    var onSelect: ((value: Int) -> Unit)? = null

    private lateinit var buttons: ArrayList<RadioButton>

    var selection = 0
        set(value) {
            field = value
            handleSelection()
        }

    init {
        attrs?.let { it ->
            val attributes = context.obtainStyledAttributes(it, R.styleable.RadioGroup)

            selection = attributes.getInteger(R.styleable.RadioGroup_selection, 0)

            attributes.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        buttons = arrayListOf()
        for (i in 0..childCount) {
            val child = getChildAt(i)
            if (child is RadioButton) {
                child.setOnSingleClickListener {
                    selection = i
                    onSelect?.invoke(i)
                }
                buttons.add(child)
            }
        }
        handleSelection()
    }

    private fun handleSelection() {
        if (!this::buttons.isInitialized) return

        for (i in 0..<buttons.size) {
            val button = buttons[i]
            button.isSelected = i == selection
        }
    }
}
