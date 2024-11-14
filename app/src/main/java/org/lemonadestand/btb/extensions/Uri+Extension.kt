package org.lemonadestand.btb.extensions

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Size
import org.lemonadestand.btb.App
import org.lemonadestand.btb.utils.FileHelper
import java.io.FileInputStream

fun Uri.getDataColumn(context: Context, selection: String?, selectionArgs: Array<String>?): String? {
	var cursor: Cursor? = null
	val column = "_data"
	val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Audio.Media.DATA, MediaStore.Video.Media.DATA)
	try {
		cursor = this.let {
			context.contentResolver.query(it, projection, selection, selectionArgs, null)
		}
		if (cursor != null && cursor.moveToFirst()) {
			val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
			val result = cursor.getString(columnIndex)
			return result
		}
	} catch (exception: Exception) {
		exception.printStackTrace()
	} finally {
		cursor?.close()
	}
	return null
}

fun Uri.isExternalStorageDocument(): Boolean {
	return "com.android.externalstorage.documents" == this.authority
}

fun Uri.isDownloadsDocument(): Boolean {
	return "com.android.providers.downloads.documents" == this.authority
}

fun Uri.isMediaDocument(): Boolean {
	return "com.android.providers.media.documents" == this.authority
}

fun Uri.filePath(): String? {
	val isKitKatorAbove = true

	// DocumentProvider
	if (isKitKatorAbove && DocumentsContract.isDocumentUri(App.instance, this)) {
		// ExternalStorageProvider
		if (isExternalStorageDocument()) {
			val docId = DocumentsContract.getDocumentId(this)
			val split = docId.split(":".toRegex()).toTypedArray()
			val type = split[0]
			if ("primary".equals(type, ignoreCase = true)) {
				return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
			}

		} else if (isDownloadsDocument()) {
			val id = DocumentsContract.getDocumentId(this)
			val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
			return contentUri.getDataColumn(App.instance, null, null)
		} else if (isMediaDocument()) {
			val docId = DocumentsContract.getDocumentId(this)
			val split = docId.split(":".toRegex()).toTypedArray()
			val type = split[0]
			var contentUri: Uri? = null
			if ("image" == type) {
				contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			} else if ("video" == type) {
				contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
			} else if ("audio" == type) {
				contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
			}
			val selection = "_id=?"
			val selectionArgs = arrayOf(split[1])
			return contentUri?.getDataColumn(App.instance, selection, selectionArgs)
		}
	} else if ("content".equals(this.scheme, ignoreCase = true)) {
		return getDataColumn(App.instance, null, null)
	} else if ("file".equals(this.scheme, ignoreCase = true)) {
		return this.path
	}
	return null
}

fun Uri.fileExtension(): String? {
	val filePath = filePath() ?: return null
	return filePath.substring(filePath.lastIndexOf(".")+1)
}

fun Uri.fileName(): String? {
	val filePath = filePath() ?: return null
	val fileExtension = fileExtension() ?: return null
	var fileName =  filePath.substring(filePath.lastIndexOf("/")+1)
	fileName = fileName.replace(".${fileExtension}", "")
	return fileName
}

fun Uri.imageDimension(): Size? {
	val mimeType = FileHelper.mimeType(this) ?: return null
	if (!mimeType.contains("image/")) return null
	val filePath = filePath() ?: return null

	val options = BitmapFactory.Options()
	options.inJustDecodeBounds = true
	BitmapFactory.decodeFile(filePath, options)
	return Size(options.outWidth, options.outHeight)
}

fun Uri.videoDimension(): Size? {
	val mimeType = FileHelper.mimeType(this) ?: return null
	if (!mimeType.contains("video/")) return null
	val filePath = filePath() ?: return null

	val retriever = MediaMetadataRetriever()
	val inputStream = FileInputStream(filePath)
	retriever.setDataSource(inputStream.fd)
	val bitmap = retriever.getFrameAtTime() ?: return null
	retriever.release()
	inputStream.close()
	return Size(bitmap.width, bitmap.height)
}

fun Uri.mediaDimension(): Size? {
	return imageDimension() ?: videoDimension()
}