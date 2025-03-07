package org.lemonadestand.btb.core.manager

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.core.models.Post
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.AppreciationRequestBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.LikeRequestBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.network.RetrofitInstance
import retrofit2.Response

object PostsManager : BaseManager() {
	val posts = MutableLiveData<ArrayList<Post>>()
	val sharedPosts = MutableLiveData<ArrayList<Post>>()

	fun getPosts(
		page: Int,
		resource: String = "",
		visibility: Post.Visibility,
		community: Int = 0,
		type: String? = null,
		callback: (() -> Unit)? = null
	) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
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
			error.postValue(response)
			CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
			return@launch
		}

		val data = response.body()?.data ?: arrayListOf()
		if (page <= 1) {
			posts.postValue(data)
		} else {
			val allPosts = arrayListOf<Post>()
			allPosts.addAll(posts.value ?: arrayListOf())
			allPosts.addAll(data)
			posts.postValue(allPosts)
		}

		CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
	}

	fun addLike(likeModel: LikeBodyModel) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
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
			error.postValue(response)
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
			if (!checkInternetConnection()) {
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
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
				return@launch
			}

			val body = response.body()
			if (body?.data != null) {
				val stories = arrayListOf<Post>()
				stories.addAll(sharedPosts.value ?: arrayListOf())
				stories.add(body.data)
				sharedPosts.postValue(stories)
			}
		}

	fun resetSharedStories() = sharedPosts.postValue(arrayListOf())

	fun addAppreciation(
		appreciationRequestBody: AppreciationRequestBody,
		callback: ((response: Response<*>) -> Unit)?
	) =
		CoroutineScope(Dispatchers.IO).launch {
			if (!checkInternetConnection()) {
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
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
				return@launch
			}
		}

	fun addComment(addCommentModel: AddCommentBody) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
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
			error.postValue(response)
			ProgressDialogUtil.dismissProgressDialog()
			return@launch
		}

		commonResponseLiveData.postValue(BaseResponse(true, "Comment Saved"))
		ProgressDialogUtil.dismissProgressDialog()

		getPosts(page = 0, visibility = Post.Visibility.PUBLIC)
	}

	fun deletePost(id: String, code: String, callback: (() -> Unit)?) =
		CoroutineScope(Dispatchers.IO).launch {
			if (!checkInternetConnection()) {
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
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
				CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
				return@launch
			}

			commonResponseLiveData.postValue(response.body())
			ProgressDialogUtil.dismissProgressDialog()

			CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
		}
}