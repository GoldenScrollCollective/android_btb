package org.lemonadestand.btb.singleton

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import org.lemonadestand.btb.activities.LoginActivity
import org.lemonadestand.btb.utils.Utils
import retrofit2.Response


object Singleton {

	inline fun <reified T : Any> Activity.launchActivity(
		requestCode: Int = -1,
		options: Bundle? = null,
		noinline init: Intent.() -> Unit = {}
	) {
		val intent = newIntent<T>(this)
		intent.init()
		startActivityForResult(intent, requestCode, options)
	}

	inline fun <reified T : Any> newIntent(context: Context): Intent =
		Intent(context, T::class.java)


	fun handleResponse(response: Response<*>, activity: Activity, tag: String) {
		Log.e(tag, "error=>  $tag")
		if (response.code() == UNAUTHORIZED) {
			Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
			Utils.saveData(activity, Utils.TOKEN, "12")
			activity.launchActivity<LoginActivity> { }
			activity.finish()
		}
	}


	// ROW TOKEN
	var authToken = ""

	// API END POINTS
	const val LOGIN = "login"
	const val MAGIC = "magic"
	const val REST_POST = "rest/post"
	const val REST_EVENT = "rest/event"
	const val REST_FIELD = "rest/fieldmeta"


	const val USER_LIST = "rest/user"
	const val USER_CONTACTS = "rest/contact"
	const val COMPANY_LIST = "rest/company"
	const val ADD_LIKE = "rest/post/meta/"

	private const val UNAUTHORIZED = 401
	const val API_LIST_LIMIT = "100"


	// Api Parameter
	const val PUBLIC = "public"
	const val PRIVATE = "private"
	const val MINE = "mine"
	const val ARCHIVES = "archives"
	const val SUCCESS = "true"
	const val FAILED = "0"

	object InterestType {
		var singleLine = "Single Line"
		var multiLine = "Multi Line"
		var numberInput = "Number Input"
		var radio = "Radio"
		var checkBox = "Checkbox"
		var dropdownSelect = "Dropdown Select"
		var datePicker = "Date Picker"
		var colorPicker = "Color Picker"
		var phoneNumber = "Phone Number"
		var fileUpload = "File Upload"
	}
}