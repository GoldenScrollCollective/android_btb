package org.lemonadestand.btb.activities

import android.view.View
import android.widget.Toast
import org.lemonadestand.btb.R
import org.lemonadestand.btb.components.MediaPreviewView
import org.lemonadestand.btb.components.QuillEditText
import org.lemonadestand.btb.components.UploadButton
import org.lemonadestand.btb.components.base.BaseActivity
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.handleCommonResponse
import org.lemonadestand.btb.core.manager.PostsManager
import org.lemonadestand.btb.core.models.User
import org.lemonadestand.btb.core.response.ShareStoryResponse
import org.lemonadestand.btb.databinding.ActivityShareStoryBinding
import org.lemonadestand.btb.extensions.lastPathComponent
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.features.common.models.body.ShareStoryUser
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.utils.AWSUploadHelper
import org.lemonadestand.btb.utils.Utils

class ShareStoryActivity : BaseActivity(R.layout.activity_share_story) {

	private lateinit var webView: QuillEditText

	private lateinit var mBinding: ActivityShareStoryBinding

	private var currentUser: User? = null

	private lateinit var mediaPreviewView: MediaPreviewView
	private var uploadedFileUrl: String? = null
		set(value) {
			field = value
			if (this::mediaPreviewView.isInitialized) {
				if (value == null) {
					mediaPreviewView.visibility = View.GONE
					return
				}
				mediaPreviewView.visibility = View.VISIBLE
				mediaPreviewView.url = value
			}
		}

	override fun init() {
		super.init()

		mBinding = ActivityShareStoryBinding.inflate(layoutInflater)

		mBinding.navHeaderView.onLeftPressed = { handleCancel() }
		mBinding.navHeaderView.onRightPressed = { handleSave() }

		setContentView(mBinding.root)
		getData()
		setUpViewModel()

		webView = findViewById(R.id.webView)

		mediaPreviewView = findViewById(R.id.mediaPreviewView)
		val uploadButton = findViewById<UploadButton>(R.id.uploadButton)
		uploadButton.onUploaded = { uploadedFileUrl = it }
	}

	private fun getData() {
		currentUser = Utils.getUser(this)
	}

	private fun setUpViewModel() {
		PostsManager.error.observe(this) {
			Singleton.handleResponse(response = it, this, "ReplyCommentActivity")
			ProgressDialogUtil.dismissProgressDialog()
		}
		PostsManager.commonResponse.observe(this) {
			handleCommonResponse(this, it)
			ProgressDialogUtil.dismissProgressDialog()
			finish()
		}
		PostsManager.noInternet.observe(this) {
			Toast.makeText(this, " $it", Toast.LENGTH_SHORT).show()
			ProgressDialogUtil.dismissProgressDialog()
		}
	}

	private fun save(html: String) {
		if (html.isEmpty() || html == "<p><br></p>") {
			Toast.makeText(this, "Please write a message.", Toast.LENGTH_SHORT).show()
			return
		}

		val currentUser = Utils.getUser(this) ?: return

		val requestBody = ShareStoryBody(
			uniq_id = "",
			resource = "user/${currentUser.uniqueId}",
			html = html,
			media = uploadedFileUrl?.lastPathComponent(),
			created = "",
			parent_id = "",
			modified = "",
			by_user_id = "",
			visibility = if (mBinding.switchIsPrivate.isChecked) "private" else "public",
			user = ShareStoryUser(id = "", name = ""),
			anonymous = if (mBinding.shareAnonymouslySwitch.isChecked) "1" else "0"
		)
		PostsManager.shareStory(requestBody) { response ->
			handleCommonResponse(this, response.body() as ShareStoryResponse)
			ProgressDialogUtil.dismissProgressDialog()
			
			if (!response.isSuccessful) {
				return@shareStory
			}

			finish()
		}
	}

	private fun handleSave() {
		mBinding.webView.onReceivedHtml = { save(it) }
		mBinding.webView.requestHtml()
	}

	private fun handleCancel() {
		AWSUploadHelper.delete(uploadedFileUrl)
		onBackPressedDispatcher.onBackPressed()
	}
}