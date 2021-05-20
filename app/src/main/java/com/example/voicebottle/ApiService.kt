package com.example.voicebottle

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiService {
    @Headers(
            "Accept: application/json",
            "Content-Type: application/json"
    )
    @POST("register")
    fun addUser(@Body userData: SendName): Call<RegUser>

    @POST("post")
    fun addMessage(@Body userData: SendAudio): Call<RegAudio>

    @POST("get_message")
    fun getMessage(@Body userData: SendApiToken): Call<GetMessage>
}


class RestApiService {
    fun addUser(sendName: SendName, onResult: (RegUser?) -> Unit){
        val retrofit = ServiceBuilder.buildService(ApiService::class.java)
        retrofit.addUser(sendName).enqueue(
                object : Callback<RegUser> {
                    override fun onFailure(call: Call<RegUser>, t: Throwable) {
                        onResult(null)
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

    fun addMessage(sendAudio: SendAudio, onResult: (RegAudio?) -> Unit) {
        val retrofit = ServiceBuilder.buildService(ApiService::class.java)
        retrofit.addMessage(sendAudio).enqueue(
            object : Callback<RegAudio> {
                override fun onFailure(call: Call<RegAudio>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<RegAudio>, response: Response<RegAudio>) {
                    val addedAudio = response.body()
                    onResult(addedAudio)
                }
            }
        )
    }

    fun getMessage(sendApiToken: SendApiToken, onResult: (GetMessage?) -> Unit) {
        val retrofit = ServiceBuilder.buildService(ApiService::class.java)
        retrofit.getMessage(sendApiToken).enqueue(
        object : Callback<GetMessage> {
                override fun onFailure(call: Call<GetMessage>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<GetMessage>, response: Response<GetMessage>) {
                    val gotMessage = response.body()
                    onResult(gotMessage)
                    println("onResponse")
                    println(gotMessage)
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


data class RegUser(
        var success: Boolean,
        var data: UserData,
        var message: String
)

data class RegAudio(
    var success: Boolean,
    var data: AudioData,
    var message: String
)

data class GetMessage(
    var success: Boolean,
    var data: ReceiveData,
    var message: String
)

data class SendName(
    var name: String
)

data class SendApiToken(
    var api_token: String?
)

data class SendAudio(
    var to_id: String?,
    var api_token: String,
    var audio_path: String,
    var audio_content: String,
)

data class UserData (
        var id: String,
        var password: String,
        var name: String,
        var api_token: String
)

data class AudioData (
    var id: String?,
    var from_id: String?,
    var audio_path: String?,
    var delivered: Boolean?,
    )

data class ReceiveData (
    var from_id: String,
    var audio_path: String,
    var audio_content: String,
    var sender_name: String,
    var created_at: String,
    )