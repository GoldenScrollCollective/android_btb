package org.lemonadestand.btb.extenstions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.setImageUrl(url: String) {
    Glide.with(context).load(url).into(this)
}