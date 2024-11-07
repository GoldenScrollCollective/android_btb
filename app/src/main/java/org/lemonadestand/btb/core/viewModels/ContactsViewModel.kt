package org.lemonadestand.btb.core.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.core.models.CompanyListResponseModel
import org.lemonadestand.btb.core.models.ContactsResponseModel
import org.lemonadestand.btb.core.repositories.CompaniesRepository
import org.lemonadestand.btb.core.repositories.ContactsRepository

class ContactsViewModel(
    app: Application,
    private val repository: ContactsRepository
): BaseViewModel<ContactsRepository>(app, repository) {
    val response: MutableLiveData<ContactsResponseModel>
        get() = repository.response

    fun getContacts(page: Int) = viewModelScope.launch {
        if (hasInternetConnection()) {
            repository.getContacts(page)
        } else {
            noInternet.postValue("No Internet Connection")
        }
    }
}