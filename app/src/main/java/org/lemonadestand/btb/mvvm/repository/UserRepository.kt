package org.lemonadestand.btb.mvvm.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.UserListResponseModel
import org.lemonadestand.btb.network.RetrofitInstance
import org.lemonadestand.btb.singleton.Singleton
import retrofit2.Response

class UserRepository {
    private var userModelResponseLive = MutableLiveData<UserListResponseModel>()
    private var contactModelResponseLive = MutableLiveData<UserListResponseModel>()
    private var commonResponseLive = MutableLiveData<CommonResponseModel>()
    private var errorLive = MutableLiveData<Response<*>>()

    val userModelResponse: LiveData<UserListResponseModel>
        get() = userModelResponseLive
    val contactModelResponse: LiveData<UserListResponseModel>
        get() = contactModelResponseLive
    val commonResponseModel: LiveData<CommonResponseModel>
        get() = commonResponseLive
    val error: LiveData<Response<*>>
        get() = errorLive

    suspend fun getUserList(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getUSerList(
                limit = Singleton.API_LIST_LIMIT,
                page = page,
            )
            if (response.isSuccessful) {
                userModelResponseLive.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                errorLive.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }

    suspend fun getContactList(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getContactList(
                limit = Singleton.API_LIST_LIMIT,
                page = page,
            )
            if (response.isSuccessful) {
                contactModelResponseLive.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                errorLive.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }


}