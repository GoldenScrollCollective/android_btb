package org.lemonadestand.btb.extensions

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

fun ImageView.setImageUrl(url: String?, @DrawableRes default: Int? = null) {
	if (url.isNullOrEmpty()) {
		if (default == null) return
		setImageResource(default)
	}

	Glide.with(context).load(url).into(this)
}