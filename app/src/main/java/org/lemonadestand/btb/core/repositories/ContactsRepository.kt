package org.lemonadestand.btb.core.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseRepository
import org.lemonadestand.btb.core.models.CompanyListResponseModel
import org.lemonadestand.btb.core.models.ContactsResponseModel
import org.lemonadestand.btb.network.RetrofitInstance

class ContactsRepository: BaseRepository<ContactsResponseModel>() {

    suspend fun getContacts(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getContacts(page = page)
            if (response.isSuccessful) {
                this@ContactsRepository.response.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                error.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }
}