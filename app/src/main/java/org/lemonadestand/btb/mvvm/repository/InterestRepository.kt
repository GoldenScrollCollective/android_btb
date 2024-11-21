package org.lemonadestand.btb.mvvm.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.features.common.models.body.UpdateInterestBody
import org.lemonadestand.btb.features.interest.models.InterestResponseModel
import org.lemonadestand.btb.network.RetrofitInstance
import retrofit2.Response

class InterestRepository {
	private var interestModelLive = MutableLiveData<InterestResponseModel>()
	private var interestModelUiLive = MutableLiveData<InterestResponseModel>()
	private var interestModelMissingLive = MutableLiveData<InterestResponseModel>()

	private var commonResponseLive = MutableLiveData<BaseResponse>()
	private var errorLive = MutableLiveData<Response<*>>()


	val interestModel: LiveData<InterestResponseModel>
		get() = interestModelLive

	val interestModelUI: LiveData<InterestResponseModel>
		get() = interestModelUiLive

	val interestModelMissing: LiveData<InterestResponseModel>
		get() = interestModelMissingLive

	val commonResponseModel: LiveData<BaseResponse>
		get() = commonResponseLive
	val error: LiveData<Response<*>>
		get() = errorLive

	suspend fun getInterestData(resource: String) {
		CoroutineScope(Dispatchers.IO).launch {


			val response = RetrofitInstance.api.getInterestData(
				resource = "user/$resource/interests",
			)
			if (response.isSuccessful) {
				interestModelLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun getInterestMissingData(resource: String, fieldIdList: ArrayList<Int>) {
		CoroutineScope(Dispatchers.IO).launch {

			val response = RetrofitInstance.api.getInterestMissingData(
				resource = "user/$resource/interests",
				fieldIds = fieldIdList
			)
			if (response.isSuccessful) {
				interestModelMissingLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun updateField(fieldId: String, body: UpdateInterestBody) {
		CoroutineScope(Dispatchers.IO).launch {
			val gson = Gson()
			val json = gson.toJson(body)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.updateField(
				field = fieldId,
				requestBody = requestBody
			)
			if (response.isSuccessful) {
				commonResponseLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun getInterestUiData() {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getInterestUiData()
			if (response.isSuccessful) {
				interestModelUiLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

}