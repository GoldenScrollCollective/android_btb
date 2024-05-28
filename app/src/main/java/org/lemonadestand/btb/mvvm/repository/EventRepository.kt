package org.lemonadestand.btb.mvvm.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.lemonadestand.btb.constants.ProgressDialogUtil
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.body.PastEventBody
import org.lemonadestand.btb.features.common.models.body.RecordRequestBody
import org.lemonadestand.btb.features.common.models.body.ScheduleBody
import org.lemonadestand.btb.features.event.models.EventResponseModel
import org.lemonadestand.btb.features.common.models.body.ReminderRequestBody
import org.lemonadestand.btb.network.RetrofitInstance
import retrofit2.Response

class EventRepository {
    private var pastEventModelLive = MutableLiveData<EventResponseModel>()
    private var scheduleEventModelLive = MutableLiveData<EventResponseModel>()
    private var recordEventModelLive = MutableLiveData<EventResponseModel>()
    private var commonResponseLive = MutableLiveData<CommonResponseModel>()
    private var errorLive = MutableLiveData<Response<*>>()

    val pastEventModel: LiveData<EventResponseModel>
        get() = pastEventModelLive

    val scheduleEventModel: LiveData<EventResponseModel>
        get() = scheduleEventModelLive

    val recordEventModel: LiveData<EventResponseModel>
        get() = recordEventModelLive

    val commonResponseModel: LiveData<CommonResponseModel>
        get() = commonResponseLive
    val error: LiveData<Response<*>>
        get() = errorLive

    suspend fun getPastEventList(model: PastEventBody) {
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
                pastEventModelLive.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                errorLive.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }
    suspend fun getScheduleEventList(model: ScheduleBody) {
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
                scheduleEventModelLive.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                errorLive.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }
    suspend fun getRecordEventList(model: ScheduleBody) {
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
                recordEventModelLive.postValue(response.body())
                ProgressDialogUtil.dismissProgressDialog()
            } else {
                errorLive.postValue(response)
                ProgressDialogUtil.dismissProgressDialog()
            }
        }
    }

    suspend fun deleteEvent(uniqueID : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.deleteEvent(
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

    suspend fun addReminder(reminderBody : ReminderRequestBody) {

        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val json = gson.toJson(reminderBody)
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
            val response = RetrofitInstance.api.addReminder(
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

    suspend fun addRecord(recordBody : RecordRequestBody) {

        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val json = gson.toJson(recordBody)
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
            val response = RetrofitInstance.api.addRecord(
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
    suspend fun editRecord(recordBody : RecordRequestBody, eventId : String) {

        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val json = gson.toJson(recordBody)
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
            val response = RetrofitInstance.api.editRecord(
                requestBody = requestBody,
                eventID = eventId

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

    suspend fun editReminder(recordBody : ReminderRequestBody, eventId : String) {

        CoroutineScope(Dispatchers.IO).launch {
            val gson = Gson()
            val json = gson.toJson(recordBody)
            val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
            val response = RetrofitInstance.api.editReminder(
                requestBody = requestBody,
                eventID = eventId
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