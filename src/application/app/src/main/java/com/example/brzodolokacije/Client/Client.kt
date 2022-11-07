package com.example.brzodolokacije.Client

import android.content.Context
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Constants.Constants
import com.example.brzodolokacije.Managers.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Client(context: Context) {

    //private lateinit var apiService: Api

    //private val client = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        //.client(client)
        .client(okhttpClient(context)) // Add our Okhttp client
        .build()

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}