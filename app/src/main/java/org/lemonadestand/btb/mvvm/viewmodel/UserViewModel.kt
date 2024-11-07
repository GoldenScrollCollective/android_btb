package org.lemonadestand.btb.mvvm.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.App
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.UserListResponseModel
import org.lemonadestand.btb.mvvm.repository.UserRepository
import retrofit2.Response

class UserViewModel(
    app: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(app) {


    init {

    }

    val liveError: LiveData<Response<*>>
        get() = userRepository.error
    val commonResponse: LiveData<CommonResponseModel>
        get() = userRepository.commonResponseModel
    val userResponseModel: LiveData<UserListResponseModel>
        get() = userRepository.userModelResponse

    val contactResponseModel : LiveData<UserListResponseModel>
        get() = userRepository.contactModelResponse

    val noInternet: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isLoading.postValue(false)
    }

     fun getUserList(page: Int) = viewModelScope.launch {
        if (hasInternetConnection()) {
            userRepository.getUserList(
                page = page,
            )
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }

    fun getContactList(page: Int) = viewModelScope.launch {
        if (hasInternetConnection()) {
            userRepository.getContactList(
                page = page,
            )
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }





    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<App>().getSystemService(
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

}