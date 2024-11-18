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
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.AppreciationRequestBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.LikeRequestBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.features.post.models.PostResponseModel
import org.lemonadestand.btb.network.RetrofitInstance
import org.lemonadestand.btb.singleton.Singleton
import retrofit2.Response

class HomeRepository {
	private var postModelLive = MutableLiveData<PostResponseModel>()
	private var commonResponseLive = MutableLiveData<CommonResponseModel>()
	private var errorLive = MutableLiveData<Response<*>>()

	val postModel: LiveData<PostResponseModel>
		get() = postModelLive
	val commonResponseModel: LiveData<CommonResponseModel>
		get() = commonResponseLive
	val error: LiveData<Response<*>>
		get() = errorLive

	suspend fun getPostList(visibility: String, page: Int) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getPostList(
				page = page,
				visibility = visibility,
				limit = Singleton.API_LIST_LIMIT,
			)
			if (response.isSuccessful) {
				postModelLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun getPosts(
		page: Int,
		resource: String = "",
		visibility: String,
		community: Int,
		type: String? = null
	) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getPosts(
				page = page,
				resource = resource,
				visibility = visibility,
				community = community,
				type = type,
			)
			if (response.isSuccessful) {
				postModelLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun addLike(likeModel: LikeBodyModel) {

		CoroutineScope(Dispatchers.IO).launch {
			val model = LikeRequestBodyModel(
				meta_name = likeModel.metaName,
				meta_value = likeModel.metaValue,
				by_user_id = likeModel.byUserId
			)
			val gson = Gson()
			val json = gson.toJson(model)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.addLike(
				requestBody = requestBody,
				uniqueId = likeModel.uniqueId,
			)
			if (response.isSuccessful) {
				var body = response.body()
				body?.message = "Like Saved"
				commonResponseLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()

				getPostList("public", 0)
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun shareStory(shareStoryModel: ShareStoryBody) {

		CoroutineScope(Dispatchers.IO).launch {

			val gson = Gson()
			val json = gson.toJson(shareStoryModel)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.shareStory(
				requestBody = requestBody,
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

	suspend fun addAppreciation(appreciationRequestBody: AppreciationRequestBody) {
		CoroutineScope(Dispatchers.IO).launch {
			val gson = Gson()
			val json = gson.toJson(appreciationRequestBody)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.addAppreciation(requestBody = requestBody)
			if (response.isSuccessful) {
				commonResponseLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun addComment(addCommentModel: AddCommentBody) {
		CoroutineScope(Dispatchers.IO).launch {

			val gson = Gson()
			val json = gson.toJson(addCommentModel)

//            val message = "The value is: $json.uniq_id"
//            Log.i("MainActivitys", message);

			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.shareStory(
				requestBody = requestBody,
			)
			if (response.isSuccessful) {
				var body = response.body()
				body?.message = "Comment Saved"
				commonResponseLive.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()

				getPostList("public", 0)
			} else {
				errorLive.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun deletePost(uniqueID: String) {

		CoroutineScope(Dispatchers.IO).launch {

			val response = RetrofitInstance.api.deletePost(
				uniqueId = uniqueID,
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


}