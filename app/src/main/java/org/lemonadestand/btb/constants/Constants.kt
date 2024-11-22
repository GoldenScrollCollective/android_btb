package org.lemonadestand.btb.constants

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import org.lemonadestand.btb.R
import org.lemonadestand.btb.core.BaseResponse
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun getDate(date: String?): String {
	date ?: return ""
	val cal = Calendar.getInstance()
	val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
	return try {
		cal.time = sdf.parse(date)!!

		val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
		val formattedDate = dateFormat.format(cal.time)
		formattedDate
	} catch (e: Exception) {
		date
	}
}

fun getDate(date: Date?): String {
	val cal = Calendar.getInstance()
	try {
		cal.time = date ?: return ""

		val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US)
		val formattedDate = dateFormat.format(cal.time)
		return formattedDate
	} catch (e: Exception) {
		return "Never"
	}
}


fun isValidHexColor(colorString: String): Boolean {
	val hexColorPattern = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})\$")
	return hexColorPattern.matches(colorString)
}

fun getDateDifference(date1: String, date2: String): Int {
	val cal = Calendar.getInstance()
	val cal2 = Calendar.getInstance()
	val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
	return try {
		cal.time = sdf.parse(date1)!!
		cal2.time = sdf.parse(date2)!!
		val d1 = LocalDate.of(cal.time.year, cal.time.month, cal.time.day)
		val d2 = LocalDate.of(cal2.time.year, cal2.time.month, cal2.time.day)
		val differenceInDays = ChronoUnit.DAYS.between(d1, d2)
		differenceInDays.toInt()
	} catch (e: Exception) {
		0
	}
}


object ProgressDialogUtil {
	private var alertDialog: Dialog? = null

	fun showProgressDialog(context: Context) {
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

	fun dismissProgressDialog() {
		alertDialog?.dismiss()
		alertDialog = null
	}
}

fun handleCommonResponse(context: Context, model: BaseResponse) {
	if (model.message.isNullOrEmpty()) return

	try {
		val layoutInflater = LayoutInflater.from(context)
		val view = layoutInflater.inflate(R.layout.custom_toast, null)

		// Customize TextView inside custom layout
		val textViewToastMessage: TextView = view.findViewById(R.id.textViewToastMessage)
		textViewToastMessage.text = model.message

		val toast = Toast(context)
		toast.duration = Toast.LENGTH_SHORT
		toast.view = view

		toast.setGravity(Gravity.START or Gravity.BOTTOM, 50, 300)

		toast.show()
	} catch (e: Exception) {
		Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show()
	}
}

fun String.getImageUrlFromName(): String {
	var name = this.lowercase(Locale.ROOT)
	if (!this.contains(" ")) {
		name = this.substring(0, 1) + " " + this.substring(1)
	}
	return "https://ui-avatars.com/api/?name=${name}"
}

enum class ClickType {
	LIKE_POST, DELETE_POST, COMMON, SELECT_USER, DELETE_EVENT, EDIT_EVENT, SingleLine,
	MultiLine,
	NumberInput,
	Radio,
	Checkbox,
	DropdownSelect,
	DatePicker,
	ColorPicker,
	PhoneNumber,
	FileUpload,
}

enum class EventClick {
	SELECT_USER, DELETE_EVENT, EDIT_EVENT, COMMON
}

enum class SelectedReminder {
	TITLE, DESCRIPTION, COMMON, NOTIFICATION, TEAM_MEMBER
}

enum class SelectedRecord {
	TITLE, DESCRIPTION, COMMON, SELECT_USER
}

fun intToHexColor(color: Int): String {
	return String.format("#%06X", 0xFFFFFF and color)
}

enum class InterestFieldType {
	UserSelect,
	SingleLine,
	MultiLine,
	NumberInput,
	Radio,
	Checkbox,
	DropdownSelect,
	DatePicker,
	ColorPicker,
	PhoneNumber,
	FileUpload,
	Common,
	AdapterClicks
}
