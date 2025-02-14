package org.lemonadestand.btb.network;

import static org.lemonadestand.btb.network.RetrofitInstance.BASEURL;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static Context context;
    private static ApiService REST_CLIENT;

    static {
        setupRestClient();
    }

    private RestClient() {
    }


    public static ApiService get() {
        return REST_CLIENT;
    }

    public static ApiService post() {
        return REST_CLIENT;
    }

    private static void setupRestClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        REST_CLIENT = retrofit.create(ApiService.class);
    }

}

