package org.lemonadestand.btb.mvvm.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.features.common.models.UserListResponseModel
import org.lemonadestand.btb.network.RetrofitInstance
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Sort
import retrofit2.Response

class UserRepository {
	private var userModelResponseLive = MutableLiveData<UserListResponseModel>()
	private var contactModelResponseLive = MutableLiveData<UserListResponseModel>()
	private var commonResponseLive = MutableLiveData<BaseResponse>()
	private var errorLive = MutableLiveData<Response<*>>()

	val userModelResponse: LiveData<UserListResponseModel>
		get() = userModelResponseLive
	val contactModelResponse: LiveData<UserListResponseModel>
		get() = contactModelResponseLive
	val commonResponseModel: LiveData<BaseResponse>
		get() = commonResponseLive
	val error: LiveData<Response<*>>
		get() = errorLive

	suspend fun getUserList(page: Int, sort: Sort, orderBy: String, query: String) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getUSerList(
				page = page,
				limit = Singleton.API_LIST_LIMIT,
				sort = sort.value,
				orderBy = orderBy,
				query = query
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