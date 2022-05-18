package com.abdurrahimgayretli.marsroverphotos.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.abdurrahimgayretli.marsroverphotos.Model.Data
import com.abdurrahimgayretli.marsroverphotos.Utilis.RetrofitRepository
import kotlinx.coroutines.Dispatchers

class MarsViewModel : ViewModel() {

    private val retrofitRepository : RetrofitRepository = RetrofitRepository()


    fun getPostCameras(rover_Name:String): LiveData<Data> {
        val dataCameras = liveData(Dispatchers.IO) {
            val marsData = retrofitRepository.getDataCameras(rover_Name)
            emit(marsData)
        }
        return dataCameras
    }

}