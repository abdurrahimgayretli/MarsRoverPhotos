package com.abdurrahimgayretli.marsroverphotos.Utilis

class RetrofitRepository {
    private var retrofitClient : RetrofitService = RetrofitClient.retrofitService

    suspend fun getDataCameras(rover_name:String) = retrofitClient.getMarsCamerasName(rover_name)
}