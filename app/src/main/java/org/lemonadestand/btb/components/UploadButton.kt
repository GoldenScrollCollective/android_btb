package org.lemonadestand.btb.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.aisynchronized.helper.Helper
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.base.BaseActivity
import org.lemonadestand.btb.utils.AWSUploadHelper
import org.lemonadestand.btb.utils.FileHelper

class UploadButton @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0,
	defStyleRes: Int = 0
): RelativeLayout(context, attrs, defStyle, defStyleRes) {
	companion object {
		val TAG: String = UploadButton::class.java.simpleName
	}

	var onUploaded: ((url: String?) -> Unit)? = null

	private lateinit var mimeType: String

	private val progressView: TextView
	private val imageView: ImageView
	private val activity: BaseActivity?
		get() {
			var context = context
			while (context is ContextWrapper) {
				if (context is Activity) {
					return context as BaseActivity
				}
				context = context.baseContext
			}
			return null
		}

	private val selectPhotoLauncher = activity!!.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
		val mimeType = FileHelper.mimeType(uri) ?: return@registerForActivityResult
		if (this.mimeType != "*" && !mimeType.contains("${this.mimeType}/")) {
			return@registerForActivityResult
		}
		handleFile(uri)
	}

	init {
		LayoutInflater.from(context).inflate(R.layout.layout_upload_view, this, true)

		progressView = findViewById(R.id.progressView)
		imageView = findViewById(R.id.imageView)

		setOnClickListener { handleClick() }

		attrs?.let { it ->
			val attributes = context.obtainStyledAttributes(it, R.styleable.UploadButton)

			val icon = attributes.getDrawable(R.styleable.UploadButton_icon)
			icon?.let { imageView.setImageDrawable(icon) }

			mimeType = attributes.getString(R.styleable.UploadButton_mimeType) ?: "image"

			attributes.recycle()
		}
	}

	private fun handleClick() {
		val permissions = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
		else if (mimeType == "*") arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
		else if (mimeType == "image") arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
		else if (mimeType == "audio") arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
		else if (mimeType == "video") arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
		else arrayOf()

		if (!Helper.checkPermissionsGranted(permissions)) {
			activity!!.requestPermissions(permissions) { granted ->
				if (granted) {
					handleClick()
				}
			}
			return
		}

		selectPhotoLauncher.launch("$mimeType/*")
	}

	private fun handleFile(uri: Uri?) {
		FileHelper.filePath(uri) ?: return

		progressView.visibility = VISIBLE
		progressView.text = "0%"
		imageView.visibility = GONE

		AWSUploadHelper.upload(uri, object : AWSUploadHelper.Callback {
			override fun onProgress(progress: Float) {
				progressView.text = "${Math.round(progress)}%"
			}

			override fun onComplete(result: String?, error: String?) {
				progressView.visibility = GONE
				imageView.visibility = VISIBLE

				if (error != null) {
					Helper.showToast(context, error)
					return
				}
				onUploaded?.invoke(result)
			}
		})
	}
}