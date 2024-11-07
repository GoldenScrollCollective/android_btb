package org.lemonadestand.btb.core.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.core.models.CompanyListResponseModel
import org.lemonadestand.btb.core.repositories.CompaniesRepository

class CompaniesViewModel(
    app: Application,
    private val repository: CompaniesRepository
): BaseViewModel<CompaniesRepository>(app, repository) {
    val response: MutableLiveData<CompanyListResponseModel>
        get() = repository.response

    fun getCompanies(page: Int) = viewModelScope.launch {
        if (hasInternetConnection()) {
            repository.getCompanies(page)
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }
}