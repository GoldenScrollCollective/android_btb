package org.lemonadestand.btb.core.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseRepository
import org.lemonadestand.btb.core.models.MemberListResponseModel
import org.lemonadestand.btb.network.RetrofitInstance
import org.lemonadestand.btb.singleton.Singleton

class MemberRepository: BaseRepository<MemberListResponseModel>() {

    suspend fun getTeams(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getTeams(
                limit = Singleton.API_LIST_LIMIT,
                page = page,
            )
            if (response.isSuccessful) {
                this@MemberRepository.response.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                error.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }
}