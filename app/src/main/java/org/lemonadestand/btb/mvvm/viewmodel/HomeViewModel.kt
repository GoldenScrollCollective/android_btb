package org.lemonadestand.btb.mvvm.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.App
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.body.AddCommentBody
import org.lemonadestand.btb.features.common.models.body.AppreciationRequestBody
import org.lemonadestand.btb.features.common.models.body.LikeBodyModel
import org.lemonadestand.btb.features.common.models.body.ShareStoryBody
import org.lemonadestand.btb.features.post.models.PostResponseModel
import org.lemonadestand.btb.mvvm.repository.HomeRepository
import retrofit2.Response

class HomeViewModel(
	app: Application,
	private val homeRepository: HomeRepository
) : AndroidViewModel(app) {

	val liveError: LiveData<Response<*>>
		get() = homeRepository.error
	val commonResponse: LiveData<CommonResponseModel>
		get() = homeRepository.commonResponseModel
	val postModel: LiveData<PostResponseModel>
		get() = homeRepository.postModel

	val noInternet: MutableLiveData<String> = MutableLiveData()
	var isLoading: MutableLiveData<Boolean> = MutableLiveData()

	init {
		isLoading.postValue(false)
	}

	fun getPostList(visibility: String, page: Int) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}

		homeRepository.getPostList(page = page, visibility = visibility)
	}

	fun getPosts(
		page: Int,
		resource: String = "",
		visibility: String,
		community: Int,
		type: String? = null
	) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		homeRepository.getPosts(
			page = page,
			resource,
			visibility = visibility,
			community = community,
			type = type
		)
	}

	fun addLike(likeModel: LikeBodyModel) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			homeRepository.addLike(likeModel)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun shareStory(model: ShareStoryBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			homeRepository.shareStory(model)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun addAppreciation(model: AppreciationRequestBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			homeRepository.addAppreciation(model)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun addComment(model: AddCommentBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			homeRepository.addComment(model)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun deletePost(uniqueId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}

		homeRepository.deletePost(uniqueId)
	}


	private fun hasInternetConnection(): Boolean {
		val connectivityManager = getApplication<App>().getSystemService(
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

}