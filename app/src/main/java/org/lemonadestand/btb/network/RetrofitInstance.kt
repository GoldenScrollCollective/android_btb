package org.lemonadestand.btb.network


import com.aisynchronized.helper.DateHelper
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RetrofitInstance {
	companion object {
		const val BASEURL = "https://api.buildthenbless.app/"
		private val retrofit by lazy {
			val gson = GsonBuilder()
//				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.registerTypeAdapter(Date::class.java, DateTypeAdapter())
				.create()
			val logging = HttpLoggingInterceptor()
			logging.setLevel(HttpLoggingInterceptor.Level.BODY)  //see the body of the response
			val client = OkHttpClient.Builder().addInterceptor(logging).build()
			Retrofit.Builder()
				.baseUrl(BASEURL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(client)
				.build()
		}
		val api: ApiService by lazy {
			retrofit.create(ApiService::class.java)
		}
	}

	private class DateTypeAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {
		private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
			timeZone = TimeZone.getTimeZone("UTC")
		}

		override fun serialize(
			src: Date?,
			typeOfSrc: Type?,
			context: JsonSerializationContext?
		): JsonElement {
			return JsonPrimitive(DateHelper.format(src, "yyyy-MM-dd HH:mm:ss"))
		}

		override fun deserialize(
			json: JsonElement?,
			typeOfT: Type?,
			context: JsonDeserializationContext?
		): Date? {
			return if (json != null) {
				dateFormat.parse(json.asString) ?: throw JsonParseException("Invalid date format")
			} else {
				null
			}
		}
	}
}