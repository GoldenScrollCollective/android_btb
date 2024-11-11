package org.lemonadestand.btb.utils

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import org.lemonadestand.btb.App
import org.lemonadestand.btb.extensions.filePath
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object FileHelper {
	private const val TAG = "FileHelper"

	private val audioDirectoryPaths = listOf(App.instance.filesDir, "BTB", "audio")
	private val audioDirectoryPath = audioDirectoryPaths.joinToString(File.separator)

	private val answerDirectoryPaths = listOf(App.instance.filesDir, "BTB", "answer")
	private val answerDirectoryPath = answerDirectoryPaths.joinToString(File.separator)

	private val restDirectoryPaths = listOf(App.instance.filesDir, "BTB", "rest")
	private val restDirectoryPath = restDirectoryPaths.joinToString(File.separator)

	init {
		try {
			val mainDir = File(audioDirectoryPath)
			if (!mainDir.exists()) {
				if (!mainDir.mkdirs()) {
					Log.e(TAG, "main directory is not ready.")
				}
			}

			val restDir = File(answerDirectoryPath)
			if (!restDir.exists()) {
				if (!restDir.mkdirs()) {
					Log.e(TAG, "rest directory is not ready.")
				}
			}
		} catch (exception: Exception) {
			exception.printStackTrace()
		}
	}

	fun filePath(uri: Uri?): String? {
		if (uri == null) return null
//		var result = uri?.path ?: return null
//		val fileName = result.substring(result.lastIndexOf("/")+1)
//		val fileExtension = if (fileName.lastIndexOf(".") < 0)  "" else fileName.substring(fileName.lastIndexOf(".") + 1)
//		if (fileExtension.isEmpty()) {
//			val projection = arrayOf(MediaStore.Audio.Media.DATA)
//			val cursor: Cursor = App.instance.contentResolver.query(uri, projection, null, null, null) ?: return result
//			val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
//			cursor.moveToFirst()
//			result = cursor.getString(columnIndex)
//			cursor.close()
//		}
//
//		return result
		return uri.filePath()
	}

	fun fileName(uri: Uri?): String? {
		val path = filePath(uri) ?: return null
		val result = path.substring(path.lastIndexOf("/")+1)
		Log.d(TAG, "fileName: $result")
		return result
	}

	fun fileName(path: String?): String? {
		return if (path == null || path.lastIndexOf("/") < 0) null else path.substring(path.lastIndexOf("/")+1)
	}

	fun fileExtension(uri: Uri?): String? {
		val fileName = fileName(uri) ?: return null
		return fileName.substring(fileName.lastIndexOf(".")+1)
	}

	fun fileExtension(path: String?): String? {
		val fileName = fileName(path) ?: return null
		return fileName.substring(fileName.lastIndexOf(".")+1)
	}
	fun uriFromFilePath(path: String?): Uri? {
		if (path.isNullOrEmpty()) return null
		return Uri.fromFile(File(path))
	}
	fun mimeType(uri: Uri?): String? {
		val path = filePath(uri) ?: return null
		return mimeType(path)
	}
	fun mimeType(path: String?): String? {
		path ?: return null
		val extension = fileExtension(path) ?: return null
		if (extension.isNotEmpty()) {
			return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
		}
		return null
	}

	private fun copy(from: Uri, destinationDir: String) {
		var bis: BufferedInputStream? = null
		var bos: BufferedOutputStream? = null

		try {
			val sourceName = fileName(from)
			val destinationFilename = destinationDir + File.separatorChar + sourceName

			val destFile = File(destinationFilename)
			if (destFile.exists()) {
				destFile.delete()
			}
			destFile.createNewFile()

			bis = BufferedInputStream(App.instance.contentResolver.openInputStream(from))
			bos = BufferedOutputStream(FileOutputStream(destinationFilename, false))
			val buf = ByteArray(1024)
			bis.read(buf)
			do {
				bos.write(buf)
			} while (bis.read(buf) != -1)
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			try {
				bis?.close()
				bos?.close()
			} catch (e: IOException) {
				e.printStackTrace()
			}
		}
	}
}