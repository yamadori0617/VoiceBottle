package com.example.voicebottle

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiService {
    //@FormUrlEncoded
    @Headers(
            "Accept: application/json",
            "Content-Type: application/json"
    )
    @POST("register")
    fun addUser(@Body userData: SendName): Call<RegUser>
    //fun addUser(@Field("name") first: String): Call<RegUser>
}


class RestApiService {
    fun addUser(sendName: SendName, onResult: (RegUser?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiService::class.java)
        retrofit.addUser(sendName).enqueue(
                object : Callback<RegUser> {

                    override fun onFailure(call: Call<RegUser>, t: Throwable) {
                        onResult(null)
                        println()
                        println("onFailure")
                        println(t.message)
                    }
                    override fun onResponse( call: Call<RegUser>, response: Response<RegUser>) {
                        val addedUser = response.body()
                        onResult(addedUser)
                        println("onResponse")
                        println(addedUser)
                    }
                }
        )
    }
}

object ServiceBuilder {
    private val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

    private val gson = GsonBuilder()
            //.serializeNulls()
            .create()

    private val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.5:8000/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}

data class SendName(
        var name: String
)

data class RegUser(
        var success: Boolean,
        var data: Data,
        var message: String
)

data class Data (
        var id: String?,
        var password: String?,
        var name: String?,
        var api_token: String?
)
