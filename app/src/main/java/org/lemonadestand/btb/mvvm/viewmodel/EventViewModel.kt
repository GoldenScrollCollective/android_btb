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
import org.lemonadestand.btb.core.response.EventResponseModel
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.common.models.body.RecordRequestBody
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.mvvm.repository.EventRepository
import retrofit2.Response

class EventViewModel(
	app: Application,
	private val eventRepository: EventRepository
) : AndroidViewModel(app) {


	init {

	}

	val liveError: LiveData<Response<*>>
		get() = eventRepository.error
	val commonResponse: LiveData<CommonResponseModel>
		get() = eventRepository.commonResponseModel
	val pastEventModel: LiveData<EventResponseModel>
		get() = eventRepository.pastEventModel

	val scheduleEventModel: LiveData<EventResponseModel>
		get() = eventRepository.scheduleEventModel

	val recordEventModel: LiveData<EventResponseModel>
		get() = eventRepository.recordEventModel

	val noInternet: MutableLiveData<String> = MutableLiveData()
	var isLoading: MutableLiveData<Boolean> = MutableLiveData()

	init {
		isLoading.postValue(false)
	}

	fun getPastEventList(model: PastEventBody) = viewModelScope.launch {
		if (hasInternetConnection()) {
			eventRepository.getPastEventList(model)
		} else {
			noInternet.postValue("No Internet Connection")
		}
	}

	fun getScheduleEventList(model: ScheduleBody) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}

		eventRepository.getScheduleEventList(model)
	}


	fun getRecordEventList(model: ScheduleBody) = viewModelScope.launch {
		if (hasInternetConnection()) {
			eventRepository.getRecordEventList(model)
		} else {
			noInternet.postValue("No Internet Connection")
		}
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

	fun deleteEvent(uniqueId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			eventRepository.deleteEvent(uniqueId)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun addReminder(reminderBody: ReminderRequestBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			eventRepository.addReminder(reminderBody)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun addRecord(reminderBody: RecordRequestBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			eventRepository.addRecord(reminderBody)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun editRecord(reminderBody: RecordRequestBody, eventId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			eventRepository.editRecord(reminderBody, eventId)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun editReminder(reminderBody: ReminderRequestBody, eventId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			eventRepository.editReminder(reminderBody, eventId)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}


}