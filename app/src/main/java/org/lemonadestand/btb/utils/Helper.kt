package com.aisynchronized.helper

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.lemonadestand.btb.App
import org.lemonadestand.btb.R
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.constants.ProgressDialogUtil.dismissProgressDialog
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import java.io.*
import java.util.*

fun Uri.toPath(): String? {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = App.instance.contentResolver?.query(this, filePathColumn, null, null, null) ?: return null
    cursor.moveToFirst()

    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
    val result = cursor.getString(columnIndex)
    cursor.close()
    return result
}

object Helper {
    var SDK_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH

    const val PERMISSION_REQUEST_CODE = 1
    private val context by lazy { App.instance }

    val screenSize by lazy {
        Size(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
    }

    fun showErrorAlert(context: Context?, sMsg: String?) {
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage(sMsg)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> }.show()
    }

    fun showMessageAlert(context: Context?, sTitle: String?, sMsg: String?) {
        AlertDialog.Builder(context)
            .setTitle(sTitle)
            .setMessage(sMsg)
            .setPositiveButton(
                "OK"
            ) { dialog, which -> }.show()
    }

    fun showToast(context: Context, messageID: Int) {
        showToast(
            context,
            context.resources.getString(messageID)
        )
    }

    fun showToast(context: Context, message: String?) {
        showCustomToast(
            context,
            message,
            Toast.LENGTH_SHORT
        )
    }

    fun showErrorToast(context: Context, messageID: Int) {
        showErrorToast(
            context,
            context.resources.getString(messageID)
        )
    }

    fun showErrorToast(context: Context, message: String?) {
        showCustomToast(
            context,
            message,
            Toast.LENGTH_LONG
        )
    }

    private fun showCustomToast(context: Context, message: String?, length: Int) {
        if (message == null) { return }
        val toast = Toast.makeText(context, message, length)
//        val textView = (toast.view as ViewGroup).getChildAt(0)
//        if (textView is TextView) {
//            val textSize = dimension(R.dimen.toast_message_text_size) // 17f
//            textView.textSize = textSize
//        }
        toast.show()
    }

    private var alertDialog: Dialog? = null
    fun showProgress(context: Context) {
        dismissProgressDialog()

        alertDialog = Dialog(context)
        alertDialog!!.requestWindowFeature(1)
        alertDialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        alertDialog!!.setContentView(R.layout.progress_dialog_layout)
        alertDialog!!.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog!!.setCanceledOnTouchOutside(false)
        alertDialog!!.setCancelable(false)
        val window: Window = alertDialog!!.window!!
        window.attributes.windowAnimations = R.style.DialogAnimation
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        alertDialog?.show()
    }

    fun hideProgress() {
        alertDialog?.dismiss()
        alertDialog = null
    }

    fun hideSoftKeyboard(context: Context) {
        if ((context as Activity).currentFocus != null) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                context.currentFocus!!.windowToken, 0
            )
        }
    }

    fun showSoftKeyboard(context: Context, view: View) {
        val inputMethodManager = (context as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }

    private fun isImmersiveAvailable(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= 19
    }

    // This snippet hides the system bars.
    fun hideSystemUI(context: Context) {
        if (!isImmersiveAvailable(
                context
            )
        ) return

        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        (context as Activity).window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    fun showSystemUI(context: Context) {
        if (!isImmersiveAvailable(
                context
            )
        ) return
        (context as Activity).window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun hideSystemUIPermanently(context: Context) {
        hideSystemUI(context)
        (context as Activity).window.decorView
            .setOnSystemUiVisibilityChangeListener {
                hideSystemUI(
                    context
                )
            }
    }

    fun isNetworkConnectionAvailable(): Boolean {
        val connectivityManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun setLanguage(localeString: String?) {
        val locale = Locale(localeString)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        val context: Context = App.instance
        context.resources
            .updateConfiguration(config, context.resources.displayMetrics)
    }

    fun writeByteToFile(bytes: ByteArray?, filePath: String?) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            val bos =
                BufferedOutputStream(FileOutputStream(file))
            bos.write(bytes)
            bos.flush()
            bos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
    fun getUniqueIMEIId(): String? {
        val context: Context = SmartFitnessApplication.instance
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return ""
            }
            val imei = telephonyManager.getDeviceId()
            return if (imei != null && !imei.isEmpty()) {
                imei
            } else {
                Build.SERIAL
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "not_found"
    }

     */

    fun randomColor(): Int {
        val red = (Math.random() * 1000 % 256).toInt()
        val green = (Math.random() * 1000 % 256).toInt()
        val blue = (Math.random() * 1000 % 256).toInt()
        return Color.rgb(red, green, blue)
    }

    fun randomIndex(scale: Int): Int {
        return (Math.random() * 1000).toInt() % scale
    }

    fun saveTemporaryImage(bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString() + "/bitmaps")
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(App.instance, arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    fun color(@ColorRes resourceId: Int): Int {
        return App.instance.resources.getColor(resourceId)
    }

    fun string(context: Context, @StringRes resourceId: Int): String {
        return context.getString(resourceId)
    }

    fun dimension(@DimenRes dimenRes: Int): Float {
        return App.instance.resources.getDimension(dimenRes)
    }

    fun dimensionSPInPx(@DimenRes dimenRes: Int): Float {
        return convertSPtoPixel(
            dimension(dimenRes)
        )
    }

    fun dimensionDPInPx(@DimenRes dimenRes: Int): Float {
        return convertDPtoPixel(
            dimension(dimenRes)
        )
    }

    fun convertDPtoPixel(dp: Float): Float {
        val resources = App.instance.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    fun convertSPtoPixel(sp: Float): Float {
        val resources = App.instance.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
    }

    fun packageName(): String {
        return App.instance.packageName
    }

    fun googlePlayStoreLink(): String {
        return "https://play.google.com/store/apps/details?id=" + packageName()
    }

    fun deviceModel(): String {
        return android.os.Build.MODEL
    }

    fun fullVersionText(): String {
        try {
            val pInfo = App.instance.packageManager.getPackageInfo(
                packageName(), 0)
            return pInfo.versionName + "." + pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    fun versionName(): String {
        try {
            val pInfo = App.instance.packageManager.getPackageInfo(packageName(), 0)
            return pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0.0"
    }

    fun getSharedPreferences(key: String, default: String): String {
        val sharedPreferences = App.instance.getSharedPreferences(App::class.java.simpleName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, default) ?: default
    }

    fun putSharedPreferences(key: String, value: String) {
        val sharedPreferences = App.instance.getSharedPreferences(App::class.java.simpleName, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).commit()
    }

    fun checkPermissionsGranted(permissions: Array<String>): Boolean {
        var granted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                granted = false
            }
        }

        return granted
    }
}