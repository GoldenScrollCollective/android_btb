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

import org.lemonadestand.btb.features.common.models.body.UpdateInterestBody
import org.lemonadestand.btb.features.interest.models.InterestResponseModel
import org.lemonadestand.btb.mvvm.repository.InterestRepository
import retrofit2.Response

class InterestViewModel(
    app: Application,
    private val interestRepository: InterestRepository
) : AndroidViewModel(app) {


    init {

    }

    val liveError: LiveData<Response<*>>
        get() = interestRepository.error
    val commonResponse: LiveData<CommonResponseModel>
        get() = interestRepository.commonResponseModel
    val interestResponseModel: LiveData<InterestResponseModel>
        get() = interestRepository.interestModel

    val interestResponseModelUi: LiveData<InterestResponseModel>
        get() = interestRepository.interestModelUI

    val interestResponseModelMissing: LiveData<InterestResponseModel>
        get() = interestRepository.interestModelMissing


    val noInternet: MutableLiveData<String> = MutableLiveData()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        isLoading.postValue(false)
    }

    fun getInterestDataList(resource: String) = viewModelScope.launch {
        if (hasInternetConnection()) {
            interestRepository.getInterestData(resource)
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }

    fun getInterestDataMissingList(resource: String,filedList : ArrayList<Int>) = viewModelScope.launch {
        if (hasInternetConnection()) {
            interestRepository.getInterestMissingData(resource,filedList)
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }
    fun updateField(fieldId: String,body : UpdateInterestBody) = viewModelScope.launch {
        if (hasInternetConnection()) {
            isLoading.postValue(true)
            interestRepository.updateField(fieldId,body)
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }
    fun getInterestDataUiList() = viewModelScope.launch {
        if (hasInternetConnection()) {
            interestRepository.getInterestUiData()
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