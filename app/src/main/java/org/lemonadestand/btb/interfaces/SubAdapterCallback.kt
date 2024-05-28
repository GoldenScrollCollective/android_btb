package org.lemonadestand.btb.interfaces

import org.lemonadestand.btb.constants.ClickType

interface OnItemClickListener {
    fun onItemClicked(`object`: Any?, index: Int, type: ClickType = ClickType.COMMON, superIndex : Int = 0)
}