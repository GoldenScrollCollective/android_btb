package org.lemonadestand.btb.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.VideoView
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.utils.FileHelper

class MediaPreviewView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0
): LinearLayout(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = MediaPreviewView::class.java.simpleName
	}

	private val container: RelativeLayout
	private val imageView: ImageView
	private val videoView: VideoView

	var url: String? = null
		set(value) {
			field = value
			handleUrl()
		}

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_media_preview, this, true)

		container = findViewById(R.id.container)
		imageView = findViewById(R.id.imageView)
		videoView = findViewById(R.id.videoView)
	}

	private fun handleUrl() {
		if (url == null) {
			container.visibility = GONE
			return
		}

		val media = url ?: return

		container.visibility = VISIBLE

		val mediaParts = media.split("_")
		if (media.contains("_w") && media.contains("_h") && mediaParts.size >= 4) {
			var strWidth = mediaParts[mediaParts.size-3]
			strWidth = strWidth.replace("w", "")
			val width = strWidth.toDouble()

			var strHeight = mediaParts[mediaParts.size-2]
			strHeight = strHeight.replace("h", "")
			val height = strHeight.toDouble()

			val containerHeight = container.measuredWidth.toDouble()  / width * height

			val layoutParams = LinearLayout.LayoutParams(container.measuredWidth, containerHeight.toInt())
//            container.layoutParams = layoutParams
		} else {
			val layoutParams = LinearLayout.LayoutParams(container.measuredWidth, 235)
			container.layoutParams = layoutParams
		}

		val mimeType = FileHelper.mimeType(url) ?: return
		if (mimeType.contains("video/")) {
			imageView.visibility = GONE
			videoView.visibility = VISIBLE

			val uri = Uri.parse(media)
			videoView.setVideoURI(uri)

			val mediaController = MediaController(context)
			mediaController.setAnchorView(videoView)
			mediaController.setMediaPlayer(videoView)
			videoView.setMediaController(mediaController)
		} else {
			imageView.visibility = VISIBLE
			videoView.visibility = GONE

			Glide.with(context).load(media).into(imageView)
		}
	}
}