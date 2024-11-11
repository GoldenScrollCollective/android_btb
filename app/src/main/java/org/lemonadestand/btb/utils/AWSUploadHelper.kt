package org.lemonadestand.btb.utils

import android.net.Uri
import android.os.Handler
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import org.lemonadestand.btb.App
import org.lemonadestand.btb.extensions.fileExtension
import org.lemonadestand.btb.extensions.fileName
import org.lemonadestand.btb.extensions.filePath
import org.lemonadestand.btb.extensions.mediaDimension
import java.io.File
import java.lang.Exception

object AWSUploadHelper {
	private const val TAG = "AWSUploadHelper"

	private const val ACCESS_KEY_ID = "AKIAQGHBAWSYHPLR27ZZ"
	private const val SECRET_ACCESS_KEY = "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"
	private const val REGION = "us-west-2"
	private const val BUCKET_NAME = "btb.uploads"
	private const val BASE_URL = "https://s3.us-west-2.amazonaws.com"

	interface Callback {
		fun onProgress(progress: Float)
		fun onComplete(result: String?, error: String?)
	}

	fun upload(fileUri: Uri?, callback: Callback? = null) {
		val currentUser = Utils.getUser(App.instance) ?: return
		val filePath = fileUri?.filePath() ?: return
		val size = fileUri.mediaDimension() ?: return

		val credentials = BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY)
		val s3Client = AmazonS3Client(credentials, Region.getRegion(Regions.US_WEST_2))
		val transferUtility = TransferUtility.builder()
			.context(App.instance)
			.s3Client(s3Client)
			.build()

		val fileName = "${fileUri.fileName()}_w${size.width}_h${size.height}_.${fileUri.fileExtension()}"
		val key = "${currentUser.uniqId}/${fileName}"

		val uploadObserver = transferUtility.upload(BUCKET_NAME, key, File(filePath))
		uploadObserver.setTransferListener(object : TransferListener {
			override fun onStateChanged(id: Int, state: TransferState?) {
				if (state == TransferState.COMPLETED) {
					Handler(App.instance.mainLooper).post { callback?.onComplete("${BASE_URL}/${BUCKET_NAME}/${key}", null) }
				} else if (state == TransferState.FAILED) {
					Handler(App.instance.mainLooper).post { callback?.onComplete(null, "Failed to upload file.") }
				}
			}

			override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
				val progress = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100.0f
				Log.d(TAG, "$progress% uploaded.")
				Handler(App.instance.mainLooper).post { callback?.onProgress(progress) }
			}

			override fun onError(id: Int, ex: Exception?) {
				Handler(App.instance.mainLooper).post { callback?.onComplete(null, ex?.localizedMessage) }
			}
		})

	}
}