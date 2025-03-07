package org.lemonadestand.btb.core.manager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.lemonadestand.btb.core.models.Event
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.common.models.body.RecordRequestBody
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.network.RetrofitInstance

object EventsManager : BaseManager() {
	private val eventsLiveData = MutableLiveData<ArrayList<Event>>(arrayListOf())
	val events: LiveData<ArrayList<Event>>
		get() = eventsLiveData

	fun getScheduledEvents(model: ScheduleBody) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		val response = RetrofitInstance.api.getScheduleEventList(
			limit = model.limit,
			page = model.page,
			completed = "0",
			orderBy = model.order_by,
			resource = model.resource,
			sort = model.sort
		)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		eventsLiveData.postValue(response.body()?.data ?: arrayListOf())
	}

	fun getPastEvents(model: PastEventBody) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		val response = RetrofitInstance.api.getPastEventList(
			limit = model.limit,
			page = model.page,
			archive = model.archive,
			completed = model.completed,
			orderBy = model.order_by,
			resource = model.resource,
			sort = model.sort
		)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		eventsLiveData.postValue(response.body()?.data ?: arrayListOf())
	}

	fun getCompletedEvents(model: ScheduleBody) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		val response = RetrofitInstance.api.getScheduleEventList(
			limit = model.limit,
			page = model.page,
			completed = "1",
			orderBy = model.order_by,
			resource = model.resource,
			sort = model.sort
		)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		eventsLiveData.postValue(response.body()?.data ?: arrayListOf())
	}

	fun deleteEvent(uniqueId: String, code: String, callback: (() -> Unit)?) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		isLoading.postValue(true)
		val response = RetrofitInstance.api.deleteEvent(uniqueId = uniqueId, code = code)
		isLoading.postValue(false)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		commonResponseLiveData.postValue(response.body())

		val events = ArrayList(eventsLiveData.value ?: arrayListOf()).filter { x -> x.uniqueId != uniqueId }
		eventsLiveData.postValue(ArrayList(events))

		CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
	}

	fun addReminder(reminderBody: ReminderRequestBody, callback: (() -> Unit)?) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		isLoading.postValue(true)
		val json = Gson().toJson(reminderBody)
		val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
		val response = RetrofitInstance.api.addReminder(
			requestBody = requestBody,
		)
		isLoading.postValue(false)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		commonResponseLiveData.postValue(response.body())

		CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
	}

	fun addRecord(recordBody: RecordRequestBody, callback: (() -> Unit)?) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		isLoading.postValue(true)
		val gson = Gson()
		val json = gson.toJson(recordBody)
		val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
		val response = RetrofitInstance.api.addRecord(
			requestBody = requestBody,
		)
		isLoading.postValue(false)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		commonResponseLiveData.postValue(response.body())

		CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
	}

	fun editRecord(recordBody: RecordRequestBody, eventId: String, callback: (() -> Unit)?) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		isLoading.postValue(true)
		val gson = Gson()
		val json = gson.toJson(recordBody)
		val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
		val response = RetrofitInstance.api.editRecord(
			requestBody = requestBody,
			eventID = eventId

		)
		isLoading.postValue(false)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		commonResponseLiveData.postValue(response.body())

		CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
	}

	fun editReminder(reminderBody: ReminderRequestBody, eventId: String, callback: (() -> Unit)?) = CoroutineScope(Dispatchers.IO).launch {
		if (!checkInternetConnection()) {
			return@launch
		}

		isLoading.postValue(true)
		val gson = Gson()
		val json = gson.toJson(reminderBody)
		val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
		val response = RetrofitInstance.api.editReminder(
			requestBody = requestBody,
			eventID = eventId
		)
		isLoading.postValue(false)

		if (!response.isSuccessful) {
			error.postValue(response)
			return@launch
		}

		commonResponseLiveData.postValue(response.body())

		CoroutineScope(Dispatchers.Main).launch { callback?.invoke() }
	}
}