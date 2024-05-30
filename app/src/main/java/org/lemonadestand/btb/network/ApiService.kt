package org.lemonadestand.btb.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.lemonadestand.btb.features.common.models.CommonResponseModel
import org.lemonadestand.btb.features.common.models.USerListResponseModel
import org.lemonadestand.btb.features.event.models.EventResponseModel
import org.lemonadestand.btb.features.interest.models.InterestResponseModel
import org.lemonadestand.btb.features.post.models.PostResponseModel
import org.lemonadestand.btb.models.LoginResponse
import org.lemonadestand.btb.singleton.Singleton
import org.lemonadestand.btb.singleton.Singleton.authToken
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {


	@GET(Singleton.REST_POST)
	suspend fun getPostList(
		@Query("limit") limit: String,
		@Query("page") page: Int,
		@Query("visibility") visibility: String,
		@Header("Authorization")
		authorization: String = authToken
	): Response<PostResponseModel>


	@GET(Singleton.USER_LIST)
	suspend fun getUSerList(
		@Query("limit") limit: String = "100",
		@Query("page") page: Int = 0,
		@Query("sort") sort: String = "asc",
		@Query("order_by") orderVy: String = "name",
		@Header("Authorization")
		authorization: String = authToken
	): Response<USerListResponseModel>

	@GET(Singleton.USER_CONTACTS)
	suspend fun getContactList(
		@Query("limit") limit: String = "100",
		@Query("page") page: Int = 0,
		@Header("Authorization")
		authorization: String = authToken
	): Response<USerListResponseModel>


	@POST(Singleton.ADD_LIKE + "{unique_id}")
	suspend fun addLike(
		@Path("unique_id") uniqueId: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@POST(Singleton.REST_POST)
	suspend fun shareStory(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@POST(Singleton.REST_POST)
	suspend fun addAppreciation(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>


	@POST(Singleton.REST_POST)
	suspend fun addComment(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@POST(Singleton.REST_EVENT)
	suspend fun addReminder(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@POST(Singleton.REST_EVENT)
	suspend fun addRecord(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@PUT(Singleton.REST_EVENT + "/" + "{event_id}")
	suspend fun editRecord(
		@Path("event_id") eventID: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>


	@GET(Singleton.REST_FIELD)
	suspend fun getInterestData(
		@Query("resource") resource: String,
		@Header("Authorization")
		authorization: String = authToken
	): Response<InterestResponseModel>

	@GET(Singleton.REST_FIELD)
	suspend fun getInterestMissingData(
		@Query("resource") resource: String,
		@Query("field_id[]") fieldIds: ArrayList<Int>,
		@Header("Authorization")
		authorization: String = authToken
	): Response<InterestResponseModel>

	@GET(Singleton.REST_FIELD)
	suspend fun getInterestUiData(
		@Query("sort") sort: String = "asc",
		@Query("order_by") orderBy: String = "position",
		@Header("Authorization")
		authorization: String = authToken
	): Response<InterestResponseModel>

	@PUT(Singleton.REST_EVENT + "/" + "{event_id}")
	suspend fun editReminder(
		@Path("event_id") eventID: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@PUT(Singleton.REST_FIELD + "/" + "{field_id}")
	suspend fun updateField(
		@Path("field_id") field: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@DELETE(Singleton.REST_POST + "/" + "{post_id}")
	suspend fun deletePost(
		@Path("post_id") uniqueId: String,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@DELETE(Singleton.REST_EVENT + "/" + "{event_id}")
	suspend fun deleteEvent(
		@Path("event_id") uniqueId: String,
		@Header("Authorization")
		authorization: String = authToken
	): Response<CommonResponseModel>

	@GET(Singleton.REST_EVENT)
	suspend fun getScheduleEventList(
		@Query("limit") limit: String?,
		@Query("page") page: String?,
		@Query("sort") sort: String?,
		@Query("order_by") orderBy: String?,
		@Query("resource") resource: String?,
		@Query("completed") completed: String?,
		@Header("Authorization") authorization: String = authToken
	): Response<EventResponseModel>

	@GET(Singleton.REST_EVENT)
	suspend fun getPastEventList(
		@Query("limit") limit: String?,
		@Query("page") page: String?,
		@Query("sort") sort: String?,
		@Query("order_by") orderBy: String?,
		@Query("resource") resource: String?,
		@Query("completed") completed: String?,
		@Query("archive") archive: String?,
		@Header("Authorization") authorization: String = authToken
	): Response<EventResponseModel>

	@POST(Singleton.LOGIN)
	fun login(@Body dataCall: JsonObject?): Call<LoginResponse?>?

	@POST(Singleton.MAGIC)
	fun forget(@Body dataCall: JsonObject?): Call<LoginResponse?>?

}