package org.lemonadestand.btb.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.VideoView
import com.bumptech.glide.Glide
import org.lemonadestand.btb.R
import org.lemonadestand.btb.features.post.models.Post

class MediaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
): LinearLayout(context, attrs, defStyle, defStyleRes) {
    companion object {
        val TAG: String = MediaView::class.java.simpleName
    }

    private val container: RelativeLayout
    private val imageView: ImageView
    private val videoView: VideoView

    var post: Post? = null
        set(value) {
            field = value
            handlePost()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_media_view, this, true)

        container = findViewById(R.id.container)
        imageView = findViewById(R.id.imageView)
        videoView = findViewById(R.id.videoView)
    }

    private fun handlePost() {
        if (post?.media == null) {
            container.visibility = GONE
            return
        }

        val media = post?.media ?: return

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

        if (media.contains(".mp4")) {
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