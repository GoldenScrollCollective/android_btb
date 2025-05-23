package org.lemonadestand.btb.core.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.lemonadestand.btb.App
import org.lemonadestand.btb.core.BaseResponse
import retrofit2.Response

open class BaseManager {
	val isLoading = MutableLiveData(false)
	val noInternet = MutableLiveData<String>()

	protected val commonResponseLiveData = MutableLiveData<BaseResponse>()
	val commonResponse: LiveData<BaseResponse>
		get() = commonResponseLiveData

	val error = MutableLiveData<Response<*>>()

	private fun hasInternetConnection(): Boolean {
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

	protected fun checkInternetConnection(): Boolean {
		val result = hasInternetConnection()
		if (!result) {
			noInternet.postValue("No Internet Connection")
		}
		return result
	}
}