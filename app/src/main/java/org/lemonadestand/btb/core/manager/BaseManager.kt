package org.lemonadestand.btb.core.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.lemonadestand.btb.App
import retrofit2.Response

open class BaseManager {
	val noInternet = MutableLiveData<String>()

	private val errorLiveData = MutableLiveData<Response<*>>()
	val error: LiveData<Response<*>>
		get() = errorLiveData

	private fun hasInternetConnection(): Boolean {
		val connectivityManager = App.instance.getSystemService(
			Context.CONNECTIVITY_SERVICE
		) as ConnectivityManager

		val activeNetwork = connectivityManager.activeNetwork ?: return false
		val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

		return when {
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
			capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
			else -> false
		}
	}

	fun checkInternetConnection(): Boolean {
		val result = hasInternetConnection()
		if (!result) {
			Toast.makeText(App.instance, "No Internet Connection", Toast.LENGTH_SHORT).show()
		}
		return result
	}
}