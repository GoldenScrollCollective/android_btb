package org.lemonadestand.btb.utils

import android.net.Uri
import android.util.Log
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest
import com.amazonaws.services.s3.model.PartETag
import com.amazonaws.services.s3.model.UploadPartRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.App
import org.lemonadestand.btb.extensions.fileExtension
import org.lemonadestand.btb.extensions.filePath
import org.lemonadestand.btb.extensions.mediaDimension
import java.io.File
import java.util.Date
import kotlin.math.min

object AWSUploadHelper {
	private const val TAG = "AWSUploadHelper"

	private const val ACCESS_KEY_ID = "AKIAQGHBAWSYHPLR27ZZ"
	private const val SECRET_ACCESS_KEY = "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"
	private const val REGION = "us-west-2"
	private const val BUCKET_NAME = "btb.uploads"
	private const val BASE_URL = "https://s3.us-west-2.amazonaws.com"

	private const val MIN_DEFAULT_PART_SIZE: Long = 5 * 1024 * 1024

	interface Callback {
		fun onProgress(progress: Int)
		fun onComplete(result: String?, error: String?)
	}

	private val s3Client: AmazonS3Client

	init {
		val credentials = BasicAWSCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY)
		s3Client = AmazonS3Client(credentials, Region.getRegion(Regions.US_WEST_2))
	}

	fun upload(fileUri: Uri?, callback: Callback? = null) {
		fileUri ?: return

		CoroutineScope(Dispatchers.IO).launch {
			val currentUser = Utils.getUser(App.instance) ?: return@launch
			val filePath = fileUri.filePath() ?: return@launch
			val size = fileUri.mediaDimension() ?: return@launch

//			val transferUtility = TransferUtility.builder()
//				.context(App.instance)
//				.s3Client(s3Client)
//				.defaultBucket(BUCKET_NAME)
//				.build()

			val fileName = "${Date().time.toInt()}_w${size.width}_h${size.height}_.${fileUri.fileExtension()}"
			val key = "${currentUser.uniqueId}/${fileName}"

			val initiateMultipartUploadRequest = InitiateMultipartUploadRequest(BUCKET_NAME, key).apply { cannedACL = CannedAccessControlList.PublicRead }
			val initiateMultipartUploadResult = s3Client.initiateMultipartUpload(initiateMultipartUploadRequest)
			val uploadId = initiateMultipartUploadResult.uploadId

			val file = File(filePath)
			var partNumber = 0
			var bytesUploaded = 0L
			val partTags = arrayListOf<PartETag>()
			while (partNumber * MIN_DEFAULT_PART_SIZE < file.length()) {
				val partSize = min(MIN_DEFAULT_PART_SIZE, file.length() - (partNumber * MIN_DEFAULT_PART_SIZE))
				val uploadPartRequest = UploadPartRequest()
					.withBucketName(BUCKET_NAME)
					.withFile(file)
					.withKey(key)
					.withUploadId(uploadId)
					.withPartNumber(partNumber + 1)
					.withFileOffset(partNumber * MIN_DEFAULT_PART_SIZE)
					.withPartSize(partSize)
				uploadPartRequest.setGeneralProgressListener { event ->
					bytesUploaded += event.bytesTransferred
					val percent = Math.round((bytesUploaded * 100f) / file.length())
					Log.d(TAG, "upload: $percent")
					CoroutineScope(Dispatchers.Main).launch { callback?.onProgress(percent) }
				}
				val uploadPartResult = s3Client.uploadPart(uploadPartRequest)
				partTags.add(uploadPartResult.partETag)

				partNumber++
			}

			val completeRequest = CompleteMultipartUploadRequest(BUCKET_NAME, key, uploadId, partTags)
			s3Client.completeMultipartUpload(completeRequest)

			CoroutineScope(Dispatchers.Main).launch { callback?.onComplete("${BASE_URL}/${BUCKET_NAME}/${key}", null) }
		}


//		val uploadObserver = transferUtility.upload(BUCKET_NAME, key, File(filePath), CannedAccessControlList.PublicRead)
//		uploadObserver.setTransferListener(object : TransferListener {
//			override fun onStateChanged(id: Int, state: TransferState?) {
//				if (state == TransferState.COMPLETED) {
//					CoroutineScope(Dispatchers.Main).launch { callback?.onComplete("${BASE_URL}/${BUCKET_NAME}/${key}", null) }
//				} else if (state == TransferState.FAILED) {
//					CoroutineScope(Dispatchers.Main).launch { callback?.onComplete(null, "Failed to upload file.") }
//				}
//			}
//
//			override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//				val progress = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100.0f
//				Log.d(TAG, "$progress% uploaded.")
//				CoroutineScope(Dispatchers.Main).launch { callback?.onProgress(progress) }
//			}
//
//			override fun onError(id: Int, ex: Exception?) {
//				CoroutineScope(Dispatchers.Main).launch { callback?.onComplete(null, ex?.localizedMessage) }
//			}
//		})

	}

	fun delete(fileUri: String?) {
		fileUri ?: return

		CoroutineScope(Dispatchers.IO).launch {
			val currentUser = Utils.getUser(App.instance) ?: return@launch
			val parts = fileUri.split(currentUser.uniqueId)
			val key = "${currentUser.uniqueId}${parts.last()}"
			s3Client.deleteObject(BUCKET_NAME, key)
		}
	}
}