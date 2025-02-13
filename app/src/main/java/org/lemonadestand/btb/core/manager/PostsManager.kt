package org.lemonadestand.btb.core.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.lemonadestand.btb.App
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.core.response.PostResponseModel
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.AppreciationRequestBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.LikeRequestBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.network.RetrofitInstance
import retrofit2.Response

object PostsManager {
	private val postResponseLiveData = MutableLiveData<PostResponseModel>()
	val posts: LiveData<PostResponseModel>
		get() = postResponseLiveData

	private val sharedPostsLiveData = MutableLiveData<ArrayList<Post>>(arrayListOf())
	val sharedPosts: LiveData<ArrayList<Post>>
		get() = sharedPostsLiveData

	private val commonResponseLiveData = MutableLiveData<BaseResponse>()
	val commonResponse: LiveData<BaseResponse>
		get() = commonResponseLiveData

	private val errorLiveData = MutableLiveData<Response<*>>()
	val error: LiveData<Response<*>>
		get() = errorLiveData

	val noInternet = MutableLiveData<String>()
	val isLoading = MutableLiveData(false)

	private fun hasInternetConnection(): Boolean {
		val connectivityManager = App.instance.getSystemService(
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

	fun getPosts(
		page: Int,
		resource: String = "",
		visibility: Post.Visibility,
		community: Int = 0,
		type: String? = null
	) = CoroutineScope(Dispatchers.IO).launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}

		val response = RetrofitInstance.api.getPosts(
			page = page,
			resource = resource,
			visibility = visibility.value,
			community = community,
			type = type,
		)

		if (!response.isSuccessful) {
			errorLiveData.postValue(response)
			ProgressDialogUtil.dismissProgressDialog()
			return@launch
		}

		postResponseLiveData.postValue(response.body())
		ProgressDialogUtil.dismissProgressDialog()
	}

	fun addLike(likeModel: LikeBodyModel) = CoroutineScope(Dispatchers.IO).launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}

		isLoading.postValue(true)

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
		isLoading.postValue(false)
		if (!response.isSuccessful) {
			errorLiveData.postValue(response)
			ProgressDialogUtil.dismissProgressDialog()
			return@launch
		}

		commonResponseLiveData.postValue(BaseResponse(status = true, message = "Like Saved"))
		ProgressDialogUtil.dismissProgressDialog()

		getPosts(page = 0, visibility = Post.Visibility.PUBLIC)
	}

	fun shareStory(
		shareStoryModel: ShareStoryBody,
		callback: ((response: Response<*>) -> Unit)?
	) =
		CoroutineScope(Dispatchers.IO).launch {
			if (!hasInternetConnection()) {
				noInternet.postValue("No Internet Connection")
				return@launch
			}

			isLoading.postValue(true)
			val gson = Gson()
			val json = gson.toJson(shareStoryModel)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.shareStory(
				requestBody = requestBody,
			)
			isLoading.postValue(false)

			CoroutineScope(Dispatchers.Main).launch { callback?.invoke(response) }

			if (!response.isSuccessful) {
				errorLiveData.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
				return@launch
			}

			val body = response.body()
			if (body?.data != null) {
				val stories = arrayListOf<Post>()
				stories.addAll(sharedPostsLiveData.value ?: arrayListOf())
				stories.add(body.data)
				sharedPostsLiveData.postValue(stories)
			}
		}

	fun resetSharedStories() = sharedPostsLiveData.postValue(arrayListOf())

	fun addAppreciation(
		appreciationRequestBody: AppreciationRequestBody,
		callback: ((response: Response<*>) -> Unit)?
	) =
		CoroutineScope(Dispatchers.IO).launch {
			if (!hasInternetConnection()) {
				noInternet.postValue("No Internet Connection")
				return@launch
			}

			isLoading.postValue(true)
			val gson = Gson()
			val json = gson.toJson(appreciationRequestBody)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.addAppreciation(requestBody = requestBody)
			isLoading.postValue(false)

			CoroutineScope(Dispatchers.Main).launch { callback?.invoke(response) }

			if (!response.isSuccessful) {
				errorLiveData.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
				return@launch
			}
		}

	fun addComment(addCommentModel: AddCommentBody) = CoroutineScope(Dispatchers.IO).launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}

		isLoading.postValue(true)
		val gson = Gson()
		val json = gson.toJson(addCommentModel)

//            val message = "The value is: $json.uniq_id"
//            Log.i("MainActivitys", message);

		val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
		val response = RetrofitInstance.api.shareStory(
			requestBody = requestBody,
		)

		isLoading.postValue(false)
		if (!response.isSuccessful) {
			errorLiveData.postValue(response)
			ProgressDialogUtil.dismissProgressDialog()
			return@launch
		}

		commonResponseLiveData.postValue(BaseResponse(true, "Comment Saved"))
		ProgressDialogUtil.dismissProgressDialog()

		getPosts(page = 0, visibility = Post.Visibility.PUBLIC)
	}

	fun deletePost(id: String, code: String, callback: (() -> Unit)?) =
		CoroutineScope(Dispatchers.IO).launch {
			if (!hasInternetConnection()) {
				noInternet.postValue("No Internet Connection")
				return@launch
			}

			isLoading.postValue(true)
			val response = RetrofitInstance.api.deletePost(
				postId = id,
				code = code
			)
			isLoading.postValue(false)

			if (!response.isSuccessful) {
				errorLiveData.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
				CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
				return@launch
			}

			commonResponseLiveData.postValue(response.body())
			ProgressDialogUtil.dismissProgressDialog()

			CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
		}
}