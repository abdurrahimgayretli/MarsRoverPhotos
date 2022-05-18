package com.abdurrahimgayretli.marsroverphotos.Utilis


import com.abdurrahimgayretli.marsroverphotos.BuildConfig
import com.abdurrahimgayretli.marsroverphotos.Model.Data
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

object RetrofitClient {
    private const val BASE_URL = "https://api.nasa.gov/mars-photos/"
    val retrofitService: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RetrofitService::class.java)
    }
}

interface RetrofitService{
    @GET("api/v1/rovers/{rover_name}/photos?sol=1000&api_key=${BuildConfig.API_KEY}")
    fun getMarsRovers(
        @Path("rover_name") rover_name: String,
        @QueryMap page : HashMap<String,String>,
        @QueryMap filter : HashMap<String,String>
    ): Call<Data>
    @GET("api/v1/manifests/{rover_name}/?sol=1000&api_key=${BuildConfig.API_KEY}")
    suspend fun getMarsCamerasName(
        @Path("rover_name") rover_name: String
    ): Data
}