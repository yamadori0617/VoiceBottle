package com.example.voicebottle

import retrofit2.Call
import retrofit2.http.GET

interface IApiService {
    @GET("api")
    fun apiDemo(): Call<>
}