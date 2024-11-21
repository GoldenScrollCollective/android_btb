package org.lemonadestand.btb.core.viewModels


import android.app.Application
import androidx.lifecycle.LiveData
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
	val pastEventsResponse: LiveData<EventsResponse>
		get() = repository.pastEventsResponse

	val scheduledEventsResponse: LiveData<EventsResponse>
		get() = repository.scheduledEventsResponse

	val completedEventsResponse: LiveData<EventsResponse>
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

	fun deleteEvent(uniqueId: String) = viewModelScope.launch {
		isLoading.postValue(true)
		if (hasInternetConnection()) {
			noInternet.postValue("No Internet Connection")
			return@launch
		}
		repository.deleteEvent(uniqueId)
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