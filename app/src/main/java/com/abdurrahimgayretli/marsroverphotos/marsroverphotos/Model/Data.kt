package com.abdurrahimgayretli.marsroverphotos.Model

data class Data(
    val photos : ArrayList<Photo>,
    val photo_manifest: CameraName
)
data class CameraName(
    val photos: List<Cameras>
)
data class Cameras(
    val sol: Int,
    val cameras: List<String>
)
data class Photo(
    val img_src : String,
    val earth_date : String,
    val rover : Rover,
    val camera : Camera
)
data class Rover(
    val name : String,
    val landing_date : String,
    val launch_date : String,
    val status : String
)
data class Camera(
    val name : String
)