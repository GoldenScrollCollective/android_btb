package org.lemonadestand.btb.core.viewModels


import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.core.repositories.EventRepository
import org.lemonadestand.btb.core.response.EventsResponse
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.common.models.body.RecordRequestBody
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.features.common.models.body.ScheduleBody

class EventViewModel(
	app: Application,
	private val repository: EventRepository
) : BaseViewModel<EventRepository>(app, repository) {

	val commonResponse: LiveData<BaseResponse>
		get() = repository.commonResponse
	val scheduledEventsResponse: MutableLiveData<EventsResponse>
		get() = repository.scheduledEventsResponse
	val pastEventsResponse: MutableLiveData<EventsResponse>
		get() = repository.pastEventsResponse
	val completedEventsResponse: MutableLiveData<EventsResponse>
		get() = repository.completedEventsResponse

	fun getPastEvents(model: PastEventBody) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.getPastEvents(model)
	}

	fun getScheduledEvents(model: ScheduleBody) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.getScheduledEvents(model)
	}


	fun getCompletedEvents(model: ScheduleBody) = viewModelScope.launch {
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.getCompletedEvents(model)
	}

	fun deleteScheduledEvent(uniqueId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.deleteEvent(uniqueId)

		val response = scheduledEventsResponse.value ?: return@launch
		scheduledEventsResponse.postValue(
			EventsResponse(
				status = response.status,
				message = response.message,
				data = ArrayList(response.data?.filter { x -> x.uniqueId != uniqueId } ?: listOf())
			)
		)
	}

	fun deletePastEvent(uniqueId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.deleteEvent(uniqueId)

		val response = pastEventsResponse.value ?: return@launch
		pastEventsResponse.postValue(
			EventsResponse(
				status = response.status,
				message = response.message,
				data = ArrayList(response.data?.filter { x -> x.uniqueId != uniqueId } ?: listOf())
			)
		)
	}

	fun deleteCompletedEvent(uniqueId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (!hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.deleteEvent(uniqueId)

		val response = completedEventsResponse.value ?: return@launch
		completedEventsResponse.postValue(
			EventsResponse(
				status = response.status,
				message = response.message,
				data = ArrayList(response.data?.filter { x -> x.uniqueId != uniqueId } ?: listOf())
			)
		)
	}

	fun addReminder(reminderBody: ReminderRequestBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			repository.addReminder(reminderBody)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun addRecord(reminderBody: RecordRequestBody) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			repository.addRecord(reminderBody)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun editRecord(reminderBody: RecordRequestBody, eventId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			repository.editRecord(reminderBody, eventId)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}

	fun editReminder(reminderBody: ReminderRequestBody, eventId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			repository.editReminder(reminderBody, eventId)
		} else {
			noInternet.postValue("No Internet Connection")

		}
	}


}