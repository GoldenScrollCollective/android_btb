package org.lemonadestand.btb.core.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseRepository
import org.lemonadestand.btb.core.models.CompanyListResponseModel
import org.lemonadestand.btb.network.RetrofitInstance

class CompaniesRepository: BaseRepository<CompanyListResponseModel>() {

    suspend fun getCompanies(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getCompanies(page = page)
            if (response.isSuccessful) {
                this@CompaniesRepository.response.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                error.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }
}