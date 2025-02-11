package org.lemonadestand.btb.core.manager

import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.lemonadestand.btb.App
import org.lemonadestand.btb.core.models.Get2FACodeRequest
import org.lemonadestand.btb.network.RetrofitInstance

object UserManager : BaseManager() {
	fun get2FACode(email: String, callback: ((success: Boolean) -> Unit)?) =
		CoroutineScope(Dispatchers.IO).launch {
			if (!checkInternetConnection()) {
				CoroutineScope(Dispatchers.Main).launch { callback?.invoke(false) }
				return@launch
			}

			val json = Gson().toJson(Get2FACodeRequest(email))
			val request = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.get2FACode(request)
			if (!response.isSuccessful) {
				CoroutineScope(Dispatchers.Main).launch {
					Toast.makeText(App.instance, response.message(), Toast.LENGTH_SHORT).show()
					callback?.invoke(false)
				}
				return@launch
			}

			CoroutineScope(Dispatchers.Main).launch {
				callback?.invoke(true)
			}
		}
}