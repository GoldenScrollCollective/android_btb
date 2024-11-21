package org.lemonadestand.btb.core.repositories


import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.core.BaseRepository
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.core.response.EventsResponse
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.common.models.body.RecordRequestBody
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.network.RetrofitInstance

class EventRepository : BaseRepository<EventsResponse>() {
	var commonResponse = MutableLiveData<BaseResponse>()
	var pastEventsResponse = MutableLiveData<EventsResponse>()
	var scheduledEventsResponse = MutableLiveData<EventsResponse>()
	var completedEventsResponse = MutableLiveData<EventsResponse>()

	suspend fun getPastEvents(model: PastEventBody) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getPastEventList(
				limit = model.limit,
				page = model.page,
				archive = model.archive,
				completed = model.completed,
				orderBy = model.order_by,
				resource = model.resource,
				sort = model.sort
			)
			if (response.isSuccessful) {
				pastEventsResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun getScheduledEvents(model: ScheduleBody) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getScheduleEventList(
				limit = model.limit,
				page = model.page,
				completed = "0",
				orderBy = model.order_by,
				resource = model.resource,
				sort = model.sort
			)
			if (response.isSuccessful) {
				scheduledEventsResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun getCompletedEvents(model: ScheduleBody) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.getScheduleEventList(
				limit = model.limit,
				page = model.page,
				completed = "1",
				orderBy = model.order_by,
				resource = model.resource,
				sort = model.sort
			)
			if (response.isSuccessful) {
				completedEventsResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun deleteEvent(uniqueID: String) {
		CoroutineScope(Dispatchers.IO).launch {
			val response = RetrofitInstance.api.deleteEvent(
				uniqueId = uniqueID,
			)
			if (response.isSuccessful) {
				commonResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun addReminder(reminderBody: ReminderRequestBody) {

		CoroutineScope(Dispatchers.IO).launch {
			val json = Gson().toJson(reminderBody)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.addReminder(
				requestBody = requestBody,
			)
			if (response.isSuccessful) {
				commonResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun addRecord(recordBody: RecordRequestBody) {

		CoroutineScope(Dispatchers.IO).launch {
			val gson = Gson()
			val json = gson.toJson(recordBody)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.addRecord(
				requestBody = requestBody,
			)
			if (response.isSuccessful) {
				commonResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun editRecord(recordBody: RecordRequestBody, eventId: String) {

		CoroutineScope(Dispatchers.IO).launch {
			val gson = Gson()
			val json = gson.toJson(recordBody)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.editRecord(
				requestBody = requestBody,
				eventID = eventId

			)
			if (response.isSuccessful) {
				commonResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

	suspend fun editReminder(recordBody: ReminderRequestBody, eventId: String) {

		CoroutineScope(Dispatchers.IO).launch {
			val gson = Gson()
			val json = gson.toJson(recordBody)
			val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
			val response = RetrofitInstance.api.editReminder(
				requestBody = requestBody,
				eventID = eventId
			)
			if (response.isSuccessful) {
				commonResponse.postValue(response.body())
				ProgressDialogUtil.dismissProgressDialog()
			} else {
				error.postValue(response)
				ProgressDialogUtil.dismissProgressDialog()
			}
		}
	}

}