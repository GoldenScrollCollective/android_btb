package org.lemonadestand.btb.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.lemonadestand.btb.core.BaseResponse
import org.lemonadestand.btb.core.models.CompanyListResponseModel
import org.lemonadestand.btb.core.models.ContactsResponseModel
import org.lemonadestand.btb.core.models.MembersResponse
import org.lemonadestand.btb.core.response.EventsResponse
import org.lemonadestand.btb.core.response.LoginResponse
import org.lemonadestand.btb.core.response.PostResponseModel
import org.lemonadestand.btb.core.response.ShareStoryResponse
import org.lemonadestand.btb.features.common.models.UserListResponseModel
import org.lemonadestand.btb.features.interest.models.InterestResponseModel
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

	@GET(Singleton.REST_POST)
	suspend fun getPosts(
		@Query("page") page: Int,
		@Query("resource") resource: String,
		@Query("visibility") visibility: String,
		@Query("community") community: Int,
		@Query("type") type: String?,
		@Header("Authorization") authorization: String = authToken
	): Response<PostResponseModel>

	@GET(Singleton.USER_LIST)
	suspend fun getUSerList(
		@Query("limit") limit: String = "100",
		@Query("page") page: Int = 0,
		@Query("sort") sort: String = "asc",
		@Query("order_by") orderBy: String = "name",
		@Query("q") query: String = "",
		@Header("Authorization")
		authorization: String = authToken
	): Response<UserListResponseModel>

	@GET(Singleton.USER_LIST)
	suspend fun getTeams(
		@Query("limit") limit: String = "100",
		@Query("page") page: Int = 0,
		@Query("sort") sort: String = "asc",
		@Query("order_by") orderVy: String = "name",
		@Header("Authorization") authorization: String = authToken
	): Response<MembersResponse>

	@GET(Singleton.COMPANY_LIST)
	suspend fun getCompanies(
		@Query("page") page: Int = 0,
		@Query("limit") limit: Int = 25,
		@Query("sort") sort: String = "asc",
		@Query("order_by") orderBy: String = "name",
		@Header("Authorization") authorization: String = authToken
	): Response<CompanyListResponseModel>

	@GET(Singleton.USER_CONTACTS)
	suspend fun getContacts(
		@Query("limit") limit: String = "100",
		@Query("page") page: Int = 0,
		@Header("Authorization")
		authorization: String = authToken
	): Response<ContactsResponseModel>

	@GET(Singleton.USER_CONTACTS)
	suspend fun getContactList(
		@Query("limit") limit: String = "100",
		@Query("page") page: Int = 0,
		@Header("Authorization")
		authorization: String = authToken
	): Response<UserListResponseModel>


	@POST(Singleton.ADD_LIKE + "{unique_id}")
	suspend fun addLike(
		@Path("unique_id") uniqueId: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

	@POST(Singleton.REST_POST)
	suspend fun shareStory(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<ShareStoryResponse>

	@POST(Singleton.REST_POST)
	suspend fun addAppreciation(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<ShareStoryResponse>


	@POST(Singleton.REST_POST)
	suspend fun addComment(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

	@POST(Singleton.REST_EVENT)
	suspend fun addReminder(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

	@POST(Singleton.REST_EVENT)
	suspend fun addRecord(
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

	@PUT(Singleton.REST_EVENT + "/" + "{event_id}")
	suspend fun editRecord(
		@Path("event_id") eventID: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

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
	): Response<BaseResponse>

	@PUT(Singleton.REST_FIELD + "/" + "{field_id}")
	suspend fun updateField(
		@Path("field_id") field: String,
		@Body requestBody: RequestBody,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

	@POST("/rest/user/code")
	suspend fun get2FACode(
		@Body requestBody: RequestBody,
		@Header("Authorization") authorization: String = authToken
	): Response<BaseResponse>

	@DELETE(Singleton.REST_POST + "/{post_id}/{code}")
	suspend fun deletePost(
		@Path("post_id") postId: String,
		@Path("code") code: String,
		@Header("Authorization") authorization: String = authToken
	): Response<BaseResponse>

	@DELETE(Singleton.REST_EVENT + "/{event_id}/{code}")
	suspend fun deleteEvent(
		@Path("event_id") uniqueId: String,
		@Path("code") code: String,
		@Header("Authorization")
		authorization: String = authToken
	): Response<BaseResponse>

	@GET(Singleton.REST_EVENT)
	suspend fun getScheduleEventList(
		@Query("limit") limit: String?,
		@Query("page") page: String?,
		@Query("sort") sort: String?,
		@Query("order_by") orderBy: String?,
		@Query("resource") resource: String?,
		@Query("completed") completed: String?,
		@Header("Authorization") authorization: String = authToken
	): Response<EventsResponse>

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
	): Response<EventsResponse>

	@POST(Singleton.LOGIN)
	fun login(@Body dataCall: JsonObject?): Call<LoginResponse?>?

	@POST(Singleton.LOGIN)
	fun getRawToken(
		@Body dataCall: JsonObject?,
		@Header("X-API-KEY") authorization: String = ""
	): Call<LoginResponse?>?

	@POST(Singleton.MAGIC)
	fun forget(@Body dataCall: JsonObject?): Call<LoginResponse?>?

}